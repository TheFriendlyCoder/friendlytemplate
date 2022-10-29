/*
 * This Java source file was generated by the Gradle 'init' task.
 */

package friendlytemplate.utilities;

import friendlytemplate.list.LinkedList;

/** Another sample class. */
class SplitUtils {
    /**
     * another helper function.
     *
     * @param source data
     * @return value
     */
    public static LinkedList split(String source) {
        int lastFind = 0;
        int currentFind = 0;
        LinkedList result = new LinkedList();

        while ((currentFind = source.indexOf(" ", lastFind)) != -1) {
            String token = source.substring(lastFind);
            if (currentFind != -1) {
                token = token.substring(0, currentFind - lastFind);
            }

            addIfValid(token, result);
            lastFind = currentFind + 1;
        }

        String token = source.substring(lastFind);
        addIfValid(token, result);

        return result;
    }

    /**
     * another helper method.
     *
     * @param token data
     * @param list data
     */
    private static void addIfValid(String token, LinkedList list) {
        if (isTokenValid(token)) {
            list.add(token);
        }
    }

    /**
     * another sample method.
     *
     * @param token data
     * @return value
     */
    private static boolean isTokenValid(String token) {
        return !token.isEmpty();
    }
}
