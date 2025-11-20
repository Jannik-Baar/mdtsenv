package simulation.federate.handler;

import hla.rti1516e.*;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.exceptions.RTIexception;
import interpreter.Interpreter;
import interpreter.utils.ReflectionUtils;
import library.model.simulation.objects.SimulationObject;
import library.model.simulation.SimulationProperty;
import org.portico.impl.hla1516e.Rti1516eFactory;
import simulation.federate.AbstractFederate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

/**
 * Manages and handles incoming data for a simulation federate.
 * Thus, it should be part of each FederateAmbassador and know the necessary Attributes of the corresponding federate.
 * This handler uses maps to put the incoming data to the correct places inside the Federate.
 */
public class IncomingDataHandler {

    // Federate Objects
    private final AbstractFederate federate;
    private final RTIambassador rtiAmbassador;

    // HLA Objects
    private final EncoderFactory encoderFactory = new Rti1516eFactory().getEncoderFactory();

    public IncomingDataHandler(AbstractFederate federate) throws RTIexception {
        this.federate = federate;
        this.rtiAmbassador = federate.getRtiAmb();
    }

    /**
     * Ablauf:
     * DONE 1. Beim erstellen der ObjectHandles eine BiMap befüllen mit Paaren (Class of SimulationObject,ObjectClassHandle)
     * DONE 2. Beim erstellen der AttributeHandles eine weitere BiMap befüllen mit Paaren (ObjectClassHandle,AttributeHandleSet)
     * DONE 2. Wenn discoveredObjectInstance aufgerufen wird
     * DONE 2.1 suche lokale Klasse anhand des Handles aus der BiMap
     * DONE 2.2 instanziiere Objekt aus gefundener Klasse
     * DONE 2.3 speichere Objekt in BiMap (Object,ObjectInstanceHandle)
     * DONE 3. Wenn reflectAttributeValues aufgerufen wird
     * DONE 3.1 suche lokale Objektinstanz anhand des InstanceHandles aus der BiMap
     * DONE 3.2 TODO wie verbinde ich attribute handles mit den Attributen der Klasse / des Objektes?
     * DONE 3.3 dekodiere die gelieferten attribute werte
     * DONE 3.3 aktualisiere die Felder des gefunden Objektes
     */
    public void processDiscoveredObjectInstanceData(ObjectInstanceHandle objectInstanceHandle, ObjectClassHandle objectClassHandle, String objectName) {
        Class<SimulationObject> objectClass = federate.getObjectClassByHandle(objectClassHandle);
        try {
            Constructor<SimulationObject> constructor = objectClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            SimulationObject object = constructor.newInstance();
            object.setId(objectName);
            federate.cacheObjectInstance(object, objectInstanceHandle);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * This Method takes the incoming Message from the Ambassador, and correctly decodes it and puts the values into the correct Attribute
     */
    public void processAttributeUpdateData(ObjectInstanceHandle instanceHandle, AttributeHandleValueMap attributeHandleValueMap) {

        // get the object instance to update
        SimulationObject objectToUpdate = federate.getSimulationObjectInstance(instanceHandle);

        // for every attribute that has to be updated get the attributeHandle
        for (AttributeHandle attributeHandle : attributeHandleValueMap.keySet()) {

            String attributeName = federate.getAttributeNameByHandle(attributeHandle);
            Class<?> attributeType = federate.getAttributeTypeByHandle(attributeHandle);
            // TODO add proper logging
            // System.out.println("Attribute to update: name = " + attributeName + ", type = " + attributeType.getSimpleName());

            try {
                Map.Entry<Object, Field> objectAndFieldToUpdate = ReflectionUtils.getDeepObjectAndFieldByPath(objectToUpdate, attributeName);
                Field fieldToUpdate = objectAndFieldToUpdate.getValue();
                Object innerObjectToUpdate = objectAndFieldToUpdate.getKey();
                fieldToUpdate.setAccessible(true);
                SimulationProperty<Object> attributeToUpdate = (SimulationProperty<Object>) fieldToUpdate.get(innerObjectToUpdate);

                String hlaDataType;
                boolean isEnum = attributeType.isEnum();
                if (isEnum) {
                    hlaDataType = Interpreter.DATATYPE_MAP.get("String");
                } else {
                    hlaDataType = Interpreter.DATATYPE_MAP.get(attributeType.getSimpleName());
                }

                DataElement dataElement;

                // Invoke the correct "create" Method for the determined dataType
                Method m = encoderFactory.getClass().getMethod("create" + hlaDataType);
                dataElement = (DataElement) m.invoke(encoderFactory);
                dataElement.decode(attributeHandleValueMap.get(attributeHandle));

                // Invoke the getValue Method on the data Element (It is not part of DataElement, but we know that
                // the types we get will have it) to get the actual value that has been sent.
                m = dataElement.getClass().getMethod("getValue");
                Object value = m.invoke(dataElement);
                //If Attribute is Enum, create the original Value based on the String
                if (isEnum) {
                    Method valueOf = attributeType.getMethod("valueOf", String.class);
                    value = valueOf.invoke(valueOf, value);
                }
                //Make sure the incoming value has the class that our Attribute expects then set it
                if (value.getClass() == attributeType) {
                    attributeToUpdate.setSingleValue(value);
                } else if (attributeType == ArrayList.class) {
                    //if the target is an arrayList, get the list and add the data to it
                    ArrayList<Object> targetList = (ArrayList<Object>) attributeToUpdate.getValue();
                    targetList.add(value);
                }

                // DEBUG OUTPUT
                // System.out.println(
                //     (((ContainerShip) objectToUpdate).getVesselName() != null ? ((ContainerShip) objectToUpdate).getVesselName().getName() + " = " + ((ContainerShip) objectToUpdate).getVesselName().getValue() + "\n" : "") +
                //     (((ContainerShip) objectToUpdate).getEmergencyDeclared().getName()) + " = " + (((ContainerShip) objectToUpdate).getEmergencyDeclared().getValue()) + "\n"
                // );

            } catch (DecoderException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        //federate.logAttributesAsJSON();
    }

}