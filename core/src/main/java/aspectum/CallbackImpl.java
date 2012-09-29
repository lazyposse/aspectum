package aspectum;

public class CallbackImpl implements Callback {

	private int depth = 0;

	@SuppressWarnings("rawtypes")
	@Override
	public void before(Thread t, Class type, String methodName, Object[] args) {
		prn(indent(type + "." + methodName, depth));
		depth++;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void afterReturning(Thread t, Class type, String methodName,
			Object returnValue) {
		depth--;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void afterThrowing(Thread t, Class type, String methodName,
			Throwable th) {
		depth--;
	}

	private void prn(String msg) {
		System.out.println(msg);
	}

	private String indent(String msg, int depth) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < depth; i++) {
			sb.append("\t");
		}
		sb.append(msg);
		return sb.toString();
	}
}