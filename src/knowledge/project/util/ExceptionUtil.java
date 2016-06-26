package knowledge.project.util;

public class ExceptionUtil {
	public static void throwAndCatchException(String message) {
		try {
			throw new Exception(message);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
 
	/**
	 * Implement a customized assertion that can pop up as an exception.
	 */
	public static void assertAsException(boolean statement) {
		if (!statement) {
			ExceptionUtil
					.throwAndCatchException("The assertion statement is not true!");
		}
	}

	public static void assertAsException(boolean statement, String message) {
		if (!statement) {
			ExceptionUtil.throwAndCatchException(message);
		}

	}
}
