package cz.via.slidecaster.task;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import cz.via.slidecaster.BaseActivity;
import cz.via.slidecaster.exception.ApplicationException;

/**
 * Generic task to be used as internal component of Task class.
 * 
 * @param <T>
 */
public class GenericAsyncTask<T> extends AsyncTask<Void, Void, T> {

	private Task<T> task;
	private BaseActivity activity;
	private ApplicationException exception;
	private String progressDialogMessage;
	private ProgressDialog progressDialog;

	public GenericAsyncTask(BaseActivity activity, Task<T> task) {
		this.task = task;
		this.activity = activity;

	}

	public GenericAsyncTask(BaseActivity activity, Task<T> task, String progressDialogMessage) {
		this(activity, task);
		this.progressDialogMessage = progressDialogMessage;
		// activity.showSpinningWheel(); // TODO We dont need this
		showLoadingElement();
	}

	private void showLoadingElement() {
		if (progressDialogMessage == null) {
			//activity.showSpinningWheel(); // TODO We dont need this
		} else {
			progressDialog = ProgressDialog.show(activity, "", progressDialogMessage, true, true, new DialogInterface.OnCancelListener() {

				public void onCancel(DialogInterface dialog) {
					GenericAsyncTask.this.cancel(true);
					// activity.finish();
				}

			});
		}

	}

	private void hideLoadingEelement() {
		if (progressDialog == null) {
			//activity.hideSpinningWheel(); // TODO We dont need this
		} else {
			progressDialog.cancel();
			progressDialog = null;
		}
	}

	@Override
	protected T doInBackground(Void... voids) {

		try {
			T t = task.doTask();
			return t;
		} catch (ApplicationException e) {
			exception = e;
		}
		return null;

	}

	@Override
	protected void onPostExecute(T t) {
		hideLoadingEelement();
		if (exception != null) {
			// there was an error
			activity.showMsgDialog(exception.getMessage(), true);
			task.doAfterTaskFailed();
		} else {
			task.doAfterTask(t);
		}

		// activity.hideSpinningWheel();
		if (task.getInterval() > 0) {
			task.getHandler().postDelayed(task.getRunnable(), task.getInterval());
		}
	}
}
