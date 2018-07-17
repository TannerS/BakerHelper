package io.dev.tanners.bakerhelper.util;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Class for templates for snackbars
 */
public class SimpleSnackBarBuilder {

    /**
     * create snackbar and displays it
     *
     * @param view
     * @param message
     * @param length
     * @param mDismissMessage
     * @param mCallback
     */
    public static void createAndDisplaySnackBar(View view, String message, int length, String mDismissMessage,  View.OnClickListener mCallback)
    {
        final Snackbar mSnackBar = SimpleSnackBarBuilder.createSnackBar(view, message, length, mDismissMessage, mCallback);

        mSnackBar.show();
    }

    /**
     * create snackbar and displays it
     *
     * @param view
     * @param message
     * @param length
     * @param mDismissMessage
     */
    public static void createAndDisplaySnackBar(View view, String message, int length, String mDismissMessage)
    {
        final Snackbar mSnackBar = SimpleSnackBarBuilder.createSnackBar(view, message, length);

        mSnackBar.setAction(mDismissMessage, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSnackBar.dismiss();
            }
        });

        mSnackBar.show();
    }

    /**
     * create snackbar with no message
     *
     * @param view
     * @param message
     * @param length
     */
    public static Snackbar createSnackBar(View view, String message, int length)
    {
        final Snackbar mSnackBar = Snackbar.make(view, message, length);

        return mSnackBar;
    }

    /**
     * create snackbar
     *
     * @param view
     * @param message
     * @param length
     * @param mDismissMessage
     * @param mCallback
     * @return
     */
    public static Snackbar createSnackBar(View view, String message, int length, String mDismissMessage, View.OnClickListener mCallback)
    {
        final Snackbar mSnackBar = Snackbar.make(view, message, length);

        mSnackBar.setAction(mDismissMessage, mCallback);

        return mSnackBar;
    }
}
