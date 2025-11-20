package manager;

import simulation.federate.AbstractFederate;
import simulation.federate.master.MasterFederate;
import simulation.federate.interpreted.InterpretedFederate;
import library.model.simulation.objects.SimulationObject;
import library.services.geodata.MapDataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Takes all Federates and coordinates their simulation with the creation of a MasterFederate.
 * Additionally, it informs the SimulationManager if the simulation has ended.
 */
public class SimulationWatchDog implements Runnable {
    SimulationManager simulationManager;
    List<Thread> threadArrayList;
    List<AbstractFederate> federates;
    String scenarioPath;
    private MapDataProvider mapDataProvider;
    private static ArrayList<SimulationWatchDog> watchDogs;
    private Boolean simulationHasEnded;
    private int iterations;
    private MasterFederate masterFederate;

    public SimulationWatchDog(SimulationManager simulationManager, List<Thread> threadArrayList, List<AbstractFederate> federates, String scenarioPath, MapDataProvider mapDataProvider, int iterations) {
        this.simulationManager = simulationManager;
        this.threadArrayList = threadArrayList;
        this.federates = federates;
        this.scenarioPath = scenarioPath;
        this.mapDataProvider = mapDataProvider;
        this.iterations = iterations;

        for (AbstractFederate federate : federates) {
            if(federate instanceof InterpretedFederate){
                InterpretedFederate interpretedFederate = (InterpretedFederate) federate;
                MapDataProvider.addToMap(interpretedFederate.getSimulatedObject(), mapDataProvider);
            }
        }

        if (watchDogs == null) {
            watchDogs = new ArrayList<>();
        }
        SimulationWatchDog.watchDogs.add(this);
        this.simulationHasEnded = false;
    }

    private void waitForThreadsToFinish() {
        for (Thread allThread : threadArrayList) {
            try {
                allThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        this.masterFederate = new MasterFederate(this.iterations);
        Thread masterThread = new Thread(masterFederate);
        masterThread.start();
        try {
            /** wait for the masterfederate to be fully initialized **/
            synchronized (masterFederate.LOCK) {
                while (!masterFederate.isSyncPointAnnounced()) {
                    masterFederate.LOCK.wait();
                }
            }
            threadArrayList.forEach(Thread::start);
            for (AbstractFederate federate : federates) {
                /** wait for each of the federates to be fully initialized **/
                synchronized (federate.LOCK) {
                    while (!federate.isAtSyncPoint()) {
                        federate.LOCK.wait();
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /** Tell the masterFederate that everything is synced, so it "starts" the actual Simulation **/
        synchronized (masterFederate.LOCK) {
            masterFederate.setFederationFullySynced(true);
            masterFederate.LOCK.notify();
        }
        waitForThreadsToFinish();
        simulationManager.simulationFinished(scenarioPath, federates);
    }

    public Boolean hasFederate(AbstractFederate federate) {
        return this.federates.contains(federate);
    }

    public Boolean hasFederateWithObject(SimulationObject simulatedObject) {
        for (AbstractFederate federate : federates) {
            if(federate instanceof InterpretedFederate) {
                InterpretedFederate interpretedFederate = (InterpretedFederate) federate;
                if (interpretedFederate.getSimulatedObject() == simulatedObject) {
                    return true;
                }
            }
        }
        return false;
    }

    public static SimulationWatchDog getWatchDogInstance(AbstractFederate federate) {
        for (SimulationWatchDog aWatchDog : watchDogs) {
            if (aWatchDog.hasFederate(federate)) {
                return aWatchDog;
            }
        }
        return null;
    }

    public static SimulationWatchDog getWatchDogInstance(SimulationObject simulatedObject) {
        for (SimulationWatchDog aWatchDog : watchDogs) {
            if (aWatchDog.hasFederateWithObject(simulatedObject)) {
                return aWatchDog;
            }
        }
        return null;
    }

    public Boolean getSimulationHasEnded() {
        return simulationHasEnded;
    }

    public void setSimulationHasEnded(Boolean simulationHasEnded) {
        this.simulationHasEnded = simulationHasEnded;
    }

    public Boolean hasRunningThreads() {
        for (Thread aThread : this.threadArrayList) {
            if (aThread.isAlive()) {
                return true;
            }
        }
        return false;
    }

    public MapDataProvider getMapDataProvider() {
        return mapDataProvider;
    }
}
