package cz.via.slidecaster.task;

import android.os.Handler;
import cz.via.slidecaster.BaseActivity;
import cz.via.slidecaster.exception.ApplicationException;
import cz.via.slidecaster.net.WebClient;

/**
 * Base task for background long running tasks.
 */
public abstract class Task<T> {

  private Handler handler = new Handler();
  private int interval = 0;
  private Runnable runnable;

  public Task(final BaseActivity activity) {
    WebClient.setContext(activity);
	runnable = new Runnable() {
		public void run() {
	        GenericAsyncTask<T> task = new  GenericAsyncTask<T>(activity, Task.this, getProgressDialogMessage());
	        task.execute();
	        if(interval > 0) {
	          handler.removeCallbacks(runnable);
	          handler.postDelayed(runnable, interval);
	        }  
		}
    };
    runnable.run();
  }

  public Task(final BaseActivity activity, int interval) {
    this(activity); 
    //activity.addRepeatableTask(this); // TODO We dont need this
    this.interval = interval;
  }

  /**
   * Perform a computation on background result in passed to doAfterTask function.
   */
  public abstract T doTask() throws ApplicationException;

  /**
   * Action to be taken after task is finished. By default it does nothing. Override to add functionality.
   */
  public void doAfterTask(T t) {
    //handler.postDelayed(runnable, interval);
  }
  
  /**
   * Action to be taken after task failed. By default it does nothing. Override to add functionality.
   */
  public void doAfterTaskFailed() {
    
  }
  /**
   * Override to launch task in foreground mode
   */
  public String getProgressDialogMessage() {
	    return null;
  }

  public Handler getHandler() {
    return handler;
  }

  public Runnable getRunnable() {
    return runnable;
  }

  public int getInterval() {
    return interval;
  }

}
