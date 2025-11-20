//package library.model.simulation;
//
//import library.model.maritime.DriveShaft;
//import library.exceptions.AttributeNotFoundException;
//import library.model.traffic.PossibleDomains;
//import library.model.traffic.TrafficParticipant;
//
//class TrafficParticipantTest {
//
//    @org.junit.jupiter.api.Test
//    public void test() throws IllegalAccessException, AttributeNotFoundException {
////        TrafficParticipant trafficParticipant = new TrafficParticipant(true,
////                                                                       new Position(3, 2, 8),
////                                                                       new FormDummy(),
////                                                                       900001,
////                                                                       1);
//        TrafficParticipant trafficParticipant = new TrafficParticipant(true,
//                                                                       new Position(3, 2, 8),
//                                                                       new FormDummy(),
//                                                                       900001,
//                                                                       1,
//                                                                       PossibleDomains.MARITIME,
//                                                                       Double.valueOf(100),
//                                                                       Double.valueOf(10),
//                                                                       Double.valueOf(10),
//                                                                       new Position(1, 1, 1),
//                                                                       100,
//                                                                       15);
//        DriveShaft driveShaft = new DriveShaft();
//        driveShaft.addChangeableAttributeID(trafficParticipant.getRotation().getId());
//        trafficParticipant.addComponent(driveShaft);
//        Task t = new Task(trafficParticipant.getRotation().getId(), 54.4);
//        trafficParticipant.pushTask(t);
//    }
//}