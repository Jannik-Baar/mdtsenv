package library.model.dto.conditions;

public abstract class TerminationCondition<T> implements ITerminationCondition<T> {

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
}
