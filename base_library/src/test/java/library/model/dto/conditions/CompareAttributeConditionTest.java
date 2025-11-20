package library.model.dto.conditions;

import library.model.simulation.SimulationProperty;
import library.model.simulation.units.NoUnit;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompareAttributeConditionTest {

    @org.junit.jupiter.api.Test
    void compareAttributeConditionTest() {
        SimulationProperty attribute1 = new SimulationProperty(NoUnit.get(), 200, "attribute1");
        SimulationProperty attribute2 = new SimulationProperty(NoUnit.get(), 200, "attribute2");
        CompareAttributeCondition condition = new CompareAttributeCondition(null, attribute1, null, attribute2, CompareAttributeCondition.CompareOperation.EQUAL);

        //Test EQUAL
        assertTrue(condition.conditionIsMet());
        attribute2.setSingleValue(199);
        assertFalse(condition.conditionIsMet());

        //Test LESS
        condition = new CompareAttributeCondition(null, attribute1, null, attribute2, CompareAttributeCondition.CompareOperation.LESS);
        assertTrue(condition.conditionIsMet());
        attribute2.setSingleValue(201);
        assertFalse(condition.conditionIsMet());

        //Test GREATER
        condition = new CompareAttributeCondition(null, attribute1, null, attribute2, CompareAttributeCondition.CompareOperation.GREATER);
        assertTrue(condition.conditionIsMet());
        attribute2.setSingleValue(199);
        assertFalse(condition.conditionIsMet());

        //Test LESSOREQUAL
        condition = new CompareAttributeCondition(null, attribute1, null, attribute2, CompareAttributeCondition.CompareOperation.LESS_OR_EQUAL);
        assertTrue(condition.conditionIsMet());
        attribute2.setSingleValue(200);
        assertTrue(condition.conditionIsMet());
        attribute2.setSingleValue(201);
        assertFalse(condition.conditionIsMet());

        //Test GREATEROREQUAL
        condition = new CompareAttributeCondition(null, attribute1, null, attribute2, CompareAttributeCondition.CompareOperation.GREATER_OR_EQUAL);
        assertTrue(condition.conditionIsMet());
        attribute2.setSingleValue(200);
        assertTrue(condition.conditionIsMet());
        attribute2.setSingleValue(199);
        assertFalse(condition.conditionIsMet());

        //Test NOTEQUAL
        condition = new CompareAttributeCondition(null, attribute1, null, attribute2, CompareAttributeCondition.CompareOperation.NOT_EQUAL);
        assertTrue(condition.conditionIsMet());
        attribute2.setSingleValue(200);
        assertFalse(condition.conditionIsMet());

        //Test AND
        SimulationProperty attribute3 = new SimulationProperty(NoUnit.get(), true, "attribute3");
        SimulationProperty attribute4 = new SimulationProperty(NoUnit.get(), true, "attribute4");
        condition = new CompareAttributeCondition(null,attribute3,null, attribute4, CompareAttributeCondition.CompareOperation.AND);
        assertTrue(condition.conditionIsMet());
        attribute4.setSingleValue(false);
        assertFalse(condition.conditionIsMet());

        //Test OR
        condition = new CompareAttributeCondition(null, attribute3, null, attribute4, CompareAttributeCondition.CompareOperation.OR);
        assertTrue(condition.conditionIsMet());
        attribute3.setSingleValue(false);
        attribute4.setSingleValue(true);
        assertTrue(condition.conditionIsMet());
        attribute4.setSingleValue(false);
        assertFalse(condition.conditionIsMet());

        //Test for Exception if given value is not a Comparable
        assertThrows(ClassCastException.class,() ->{
            CompareAttributeCondition exeptionCondition = new CompareAttributeCondition(null, attribute3,new ArrayList[1], CompareAttributeCondition.CompareOperation.OR);
        });
        assertThrows(ClassCastException.class,() ->{
            CompareAttributeCondition exeptionCondition = new CompareAttributeCondition(null, attribute3, null, new SimulationProperty(NoUnit.get(), new ArrayList[1], "attribute5"), CompareAttributeCondition.CompareOperation.OR);
        });

    }
}
