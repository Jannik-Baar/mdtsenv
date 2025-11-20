package manager;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import interpreter.Interpreter;
import library.model.dto.scenario.ScenarioDTO;
import library.services.geodata.MapDataProvider;
import library.services.logging.LoggingService;
import library.services.scenario.ScenarioConverter;
import simulation.federate.AbstractFederate;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Timer;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static util.FileSystemUtils.isPathFile;

/**
 * The SimulationManager is used for loading and executing scenarios,
 * it provides a simple textbased Interface for the usage of multiple commands.
 */
public class SimulationManager {

    // CLI parameters
    @Parameter(names = {"--scenario", "-s"})
    private String startScenarioPath = null;

    @Parameter(names = {"--autostart", "-a"})
    private boolean autostart = false;

    @Parameter(names = {"--runs", "-r"})
    private int runs = 1;

    private final Scanner scanner;
    private final Map<String, List<AbstractFederate>> previousSimulations;
    private final Map<String, Integer> currentIndexHashMap;

    private SimulationWatchDog currentSimulationWatchDog;
    private Timer simulationStopperTimer;
    private SimulationStopperTask simulationStopperTask;
    private final List<String> scenarioPathList;
    private int scenarioPathIndex;

    private Object waitForStopLock = new Object();
    private boolean waitForStop = false;

    private Interpreter interpreter = Interpreter.getInstance();

    public static void main(String... args) {

        Logger logger = Logger.getAnonymousLogger();
        LogManager manager = LogManager.getLogManager();
        try {
            manager.readConfiguration(new FileInputStream("simulation/properties/logging.properties"));
            logger.getHandlers();
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }

        SimulationManager main = new SimulationManager();
        JCommander.newBuilder()
                  .addObject(main)
                  .build()
                  .parse(args);
        main.run();
    }

    public SimulationManager() {
        scenarioPathIndex = 0;
        scanner = new Scanner(System.in);
        previousSimulations = new HashMap<>();
        currentIndexHashMap = new HashMap<>();
        scenarioPathList = new ArrayList<>();
        simulationStopperTimer = new Timer();
        simulationStopperTask = null;
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(Level.WARNING);
        for (Handler h : rootLogger.getHandlers()) {
            h.setLevel(Level.WARNING);
        }
    }

    public void run() {
        printProgramInfo();
        boolean scenarioParameterExists = true;

        if (startScenarioPath != null && isPathFile(startScenarioPath)) {
            scenarioPathList.add(startScenarioPath);
        }

        if (autostart && !scenarioPathList.isEmpty()) {
            startNextScenario();
        }

        waitForInput();
    }

    private static void printProgramInfo() {
        // TODO get version from projects pom.xml
        System.out.println(
            """
            -----------------------------------------------
            |    PG MTSS Command Line Interface v0.0.3    |
            -----------------------------------------------
            """
        );
    }

    private static void printHelp(boolean printInfo) {
        if (printInfo) {
            printProgramInfo();
        }
        System.out.println(
            """
            Available commands:
            help                     prompts this list of commands
            scenario list            prompts a list of the currently selected scenario files
            scenario add <path>      adds <path> as scenario file
            scenario add             requests the user to select scenario file(s)
            scenario remove <index>  removes the scenario with the index printed in "scenario" command
            scenario clear           clear all scenarios from the list
            runs                     prompts the currently selected number of runs
            runs <number>            defines the number runs the simulation will be executed
            start                    starts the simulation with loaded scenario
            stop current             stops simulation of current scenario, proceeds with the next scenario
            stop all                 stops simulation of current scenario, without proceeding to the next
            status                   shows the current state of the simulation system
            exit                     exits the program
            """
        );
    }

    private void waitForInput() {
        printHelp(true);
        while (true) {
            String line = scanner.nextLine();
            Queue<String> input = new LinkedList<>(Arrays.asList(line.split("\\s+")));
            input.add("");

            switch (input.poll()) {
                case "help":
                    printHelp(true);
                    break;

                case "exit":
                    simulationStopperTimer.cancel();
                    stopSimulation(true);
                    System.out.println("Exiting...");
                    currentSimulationWatchDog = null;
                    System.exit(0);
                    break;

                case "start":
                    scenarioPathIndex = 0;
                    startNextScenario();
                    break;

                case "scenario":
                    switch (input.poll()) {
                        case "list":
                            listScenarioFiles();
                            break;
                        case "add":
                            String addParam = input.poll();
                            if (!addParam.isBlank()) {
                                if (isPathFile(addParam) && !scenarioPathExists(addParam)) {
                                    this.scenarioPathList.add(addParam);
                                } else {
                                    System.out.println("Invalid path, please try again.");
                                }
                            } else {
                                List<File> newFiles = requestScenarioFile();
                                if (newFiles != null && !newFiles.isEmpty()) {
                                    for (File f : newFiles) {
                                        if (!scenarioPathExists(f.getAbsolutePath())) {
                                            scenarioPathList.add(f.getAbsolutePath());
                                        }
                                    }
                                }
                            }
                            listScenarioFiles();
                            break;
                        case "clear":
                            scenarioPathList.clear();
                            break;
                        case "remove":
                            String param = input.poll();
                            int index = Integer.parseInt(param) - 1;
                            if (index < scenarioPathList.size()) {
                                scenarioPathList.remove(index);
                            }
                            listScenarioFiles();
                            break;
                        default:
                            System.out.println("ERROR: Missing sub-command");
                    }
                    break;

                case "runs":
                    String runsParam = input.poll();
                    if (!runsParam.isBlank()) {
                        try {
                            runs = Integer.parseInt(runsParam);
                            System.out.println("Set runs to " + runs);
                        } catch (NumberFormatException nfe) {
                            System.out.println("Unable to read number of runs, try again with valid number.");
                        }
                    } else {
                        System.out.println("Runs is set to " + runs);
                    }
                    break;

                case "stop":
                    String stopParam = input.poll();
                    switch (stopParam) {
                        case "current":
                            stopSimulation(false);
                            break;
                        case "all":
                            stopSimulation(true);
                            break;
                        default:
                            System.out.print("ERROR: Missing parameter");
                    }
                    break;

                case "status":
                    System.out.println("ERROR: NOT IMPLEMENTED YET");
                    break;

                default:
                    System.out.println("ERROR: No valid command found");
            }
        }
    }

    private boolean scenarioPathExists(String absoluteScenarioPath) {
        return scenarioPathList.stream()
                               .anyMatch(s -> s.equals(absoluteScenarioPath));
    }

    private void listScenarioFiles() {
        if (scenarioPathList.isEmpty()) {
            System.out.println("No scenarios selected.");
            return;
        }
        System.out.println("Currently selected scenario files are:"); // TODO proper logging
        for (int i = 0; i < scenarioPathList.size(); i++) {
            System.out.println((i + 1) + "   " + scenarioPathList.get(i));
        }
    }

    private void startNextScenario() {

        if (scenarioPathList.isEmpty()) {
            System.out.println("ERROR: No scenarios selected.");
            return;
        } else if (scenarioPathIndex >= scenarioPathList.size()) {
            System.out.println("INFO: All simulation runs finished.");
            scenarioPathIndex = 0;
            simulationStopperTimer.cancel();
            stopSimulation(true);
            System.out.println("Exiting...");
            currentSimulationWatchDog = null;
            System.exit(0);
            return;
        }

        String scenario = scenarioPathList.get(scenarioPathIndex);
        try {
            synchronized (waitForStopLock) {
                while (waitForStop) {
                    try {
                        waitForStopLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        throw e;
                    }
                }
                startScenario(scenario);
                scenarioPathIndex++;
            }
        } catch (InterruptedException e) {
            System.out.print("ERROR: Could not start simulation of scenario " + scenario + " because of the following problem:");
            System.out.print("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * TODO write javadoc documentation about what happens here
     *
     * @param scenarioPath
     */
    private void startScenario(String scenarioPath) {

        // if the scenario is not part of the list of scenarios to run yet, add it with a run counter of 0 (hasn't run yet)
        if (!currentIndexHashMap.containsKey(scenarioPath)) {
            currentIndexHashMap.put(scenarioPath, 0);
        }

        // if the scenario hasn't run as often as it should be (indicated by 'runs') than run it and increment the run counter
        if (currentIndexHashMap.get(scenarioPath) < runs) {

            // increase individual scenario run counter
            currentIndexHashMap.replace(scenarioPath, currentIndexHashMap.get(scenarioPath) + 1);

            // TODO proper logging
            System.out.println(scenarioPath + ": Doing simulation run " + (currentIndexHashMap.get(scenarioPath)) + " of " + runs);

            simulationStopperTimer.cancel();

            ///////////////////////////////////
            // STEP 1 "read and convert"
            ///////////////////////////////////
            ScenarioDTO scenario;
            MapDataProvider mapDataProvider;
            try {
                // let the scenario xml be converted to a valid java data model
                scenario = ScenarioConverter.convertToScenarioModel(scenarioPath);
                mapDataProvider = new MapDataProvider(scenario);
                scenario.getLoggingTypes().stream().forEach(type -> LoggingService.registerLogger(type));
            } catch (JAXBException | IOException e) {
                // if something fails, don't run anything and return instead
                e.printStackTrace();
                System.out.println("ERROR: Invalid Scenario");
                return;
            }

            ///////////////////////////////////
            // LEGACY STEP 2 "generate FOMs"
            ///////////////////////////////////

            // generate HLA compliant data model (FOM) from the previous initialized java scenario model
            // List<FOM> foms = interpreter.generateFOMs(scenario);

            // reload Scenario since its structure may have been altered by the FOM Generation
            // QUESTION: Is it inevitable that the structure will be changed by FOM generation?
            // commented this out to test if it works without re-building after the behavioural refactoring (see commit 0edbf3a)
            // 01/02/2022: seems to work without the code below... comment and code will stay here for a while in case of errors
            // 04/02/2022: first startup does not work anymore, because two different hashcodes will be generated and the FOM file is not found... investigating
            //
            // try {
            //     scenario = ScenarioConverter.convertToScenarioModel(scenarioPath);
            //     mapDataProvider = new MapDataProvider(scenario);
            // } catch (JAXBException | FileNotFoundException e) {
            //     e.printStackTrace();
            //     System.out.println("ERROR: Invalid Scenario");
            //     return;
            // }

            ///////////////////////////////////
            // STEP 2 "prepare simulation"
            ///////////////////////////////////
            List<AbstractFederate> allFederates = prepareSimulation(scenario);
            List<Thread> allThreads = prepareThreads(allFederates);

            currentSimulationWatchDog = new SimulationWatchDog(this, allThreads, allFederates, scenarioPath, mapDataProvider, scenario.getSimulationIterations());
            Thread mainThread = new Thread(currentSimulationWatchDog);
            mainThread.start();

            if (scenario.isTimeLimited()) {
                simulationStopperTimer = new Timer();
                simulationStopperTask = new SimulationStopperTask(this, scenario.getMaxDuration());
                simulationStopperTimer.schedule(simulationStopperTask, scenario.getMaxDuration());
            }

        } else { // if the scenario has been executed as many times as indicated by 'runs', go back to the start method to run the next one
            System.out.println(scenarioPath + ": All simulation runs (" + runs + ") for the current scenario " + scenarioPath + " finished"); // TODO proper logging
            startNextScenario();
        }
    }

    private List<AbstractFederate> prepareSimulation(ScenarioDTO scenario) {
        return interpreter.createFederates(scenario);
    }

    // move to watchdog?
    private List<Thread> prepareThreads(List<AbstractFederate> federateList) {
        return federateList.stream()
                           .map(federate -> new Thread(federate))
                           .collect(Collectors.toList());
    }

    private List<File> requestScenarioFile() {
        List<File> file = new ArrayList<>();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            JFileChooser jFileChooser = new JFileChooser();
            FileFilter filter = new FileNameExtensionFilter("XML Files",
                                                            "xml");
            jFileChooser.addChoosableFileFilter(filter);
            jFileChooser.setAcceptAllFileFilterUsed(false);
            jFileChooser.setMultiSelectionEnabled(true);
            jFileChooser.showOpenDialog(null);
            if (jFileChooser.getSelectedFiles().length == 0) {
                if (scenarioPathList.isEmpty()) {
                    System.out.println("No file selected, specify manually by typing the path or restart.");
                    throw new Exception();
                } else {
                    return file;
                }
            }
            file.addAll(Arrays.asList(jFileChooser.getSelectedFiles()));
        } catch (Exception ex) {
            System.out.println("Error opening file dialog.");
            return null;
        }
        return file;
    }

    // TODO has to be refactored for physically distributed simulation execution
    protected void stopSimulation(boolean stopAll) {
        waitForStop = true;
        simulationStopperTimer.cancel();

        if (stopAll) {
            scenarioPathIndex = scenarioPathList.size();
        }

        if (currentSimulationWatchDog != null) {
            currentSimulationWatchDog.setSimulationHasEnded(true);
            System.out.println("Simulation is stopping."); // TODO proper logging
            int counter = 0;
            while (currentSimulationWatchDog.hasRunningThreads()) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("There are still running Threads after " + (counter + 1) + " seconds. Waiting ...");
                if (counter == 30) {
                    break;
                }
                counter++;
            }
            if (currentSimulationWatchDog.hasRunningThreads()) {
                System.out.println("WARNING: There are still running Threads...");
            } else {
                synchronized (waitForStopLock) {
                    waitForStop = false;
                    waitForStopLock.notify();
                    System.out.println("Simulation has ended. ");
                }
            }
        }
    }

    protected void simulationFinished(String scenarioPath, List<AbstractFederate> allFederates) {
        // put 'away' the finished simulation run
        previousSimulations.put(scenarioPath, allFederates);

        // start the next simulation run (and maybe also the next scenario)
        startScenario(scenarioPath);
    }
}
