package top.yusora.tanhua.utils;

@FunctionalInterface
public interface TriFunction<T, U, R, S> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @param r the third function argument
     * @return the function result
     */
    S apply(T t, U u, R r);
}
