package algorithms;

import settings.AppPropertyTypes;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;

/**
 * This interface provides a way to run an algorithm
 * on a thread as a {@link java.lang.Runnable} object.
 *
 * @author Ritwik Banerjee
 */
public interface Algorithm extends Runnable{

    int getMaxIterations();

    int getUpdateInterval();

    boolean tocontinue();

    default void run(){
        Dialog errorDialog = ErrorDialog.getDialog();
        errorDialog.show(AppPropertyTypes.ALGORITHM_NOT_FOUND.name(),
                         AppPropertyTypes.ALGORITHM_NOT_FOUND_MESSAGE.name());
    }

}
