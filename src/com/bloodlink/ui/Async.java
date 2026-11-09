package com.bloodlink.ui;

import java.awt.Component;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Runs work off the Event Dispatch Thread and delivers results back on it. */
final class Async {
    private Async() {}

    static <T> void run(Component parent, Callable<T> work, Consumer<T> onSuccess) {
        new SwingWorker<T, Void>() {
            @Override protected T doInBackground() throws Exception { return work.call(); }
            @Override protected void done() {
                try { onSuccess.accept(get()); }
                catch (java.util.concurrent.ExecutionException e) {
                    Throwable t = e.getCause() == null ? e : e.getCause();
                    error(parent, t.getMessage() == null ? "Unexpected error." : t.getMessage());
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }.execute();
    }

    static void error(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "BloodLink", JOptionPane.ERROR_MESSAGE);
    }

    static void info(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "BloodLink", JOptionPane.INFORMATION_MESSAGE);
    }
}
