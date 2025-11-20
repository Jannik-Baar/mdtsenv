package library.model.dto.conditions;

public interface ITerminationCondition<T> {

    enum CompareOperation {
        EQUAL,
        NOT_EQUAL,
        LESS,
        LESS_OR_EQUAL,
        GREATER,
        GREATER_OR_EQUAL,
        AND,
        OR
    }

    boolean conditionIsMet();

}
