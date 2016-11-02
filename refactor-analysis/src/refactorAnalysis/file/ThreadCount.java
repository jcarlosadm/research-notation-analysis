package refactorAnalysis.file;

public class ThreadCount {

	private int numberOfThreadRunning;
	private boolean blocker;
	
	public ThreadCount(int numberOfThreadRunning, boolean blocker) {
		// TODO Auto-generated constructor stub
		this.numberOfThreadRunning = numberOfThreadRunning;
		this.blocker = blocker;
	}
	
	public void increment() {
		++this.numberOfThreadRunning;
	}
	
	public void decrement() {
		--this.numberOfThreadRunning;
	}
	
	public void unblock() {
		this.blocker = false;
	}
	
	public void block() {
		this.blocker = true;
	}
	
	public boolean isBlocked() {
		return this.blocker;
	}
	
	public int getNumberOfThreadsRunning() {
		return this.numberOfThreadRunning;
	}
	
}
