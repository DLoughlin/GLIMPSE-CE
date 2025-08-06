/*
 * LEGAL NOTICE
 * This computer software was prepared by US EPA.
 * THE GOVERNMENT MAKES NO WARRANTY, EXPRESS OR IMPLIED, OR ASSUMES ANY
 * LIABILITY FOR THE USE OF THIS SOFTWARE. This notice including this
 * sentence must appear on any copies of this computer software.
 *
 * EXPORT CONTROL
 * User agrees that the Software will not be shipped, transferred or
 * exported into any country or used in any manner prohibited by the
 * United States Export Administration Act or any other applicable
 * export laws, restrictions or regulations (collectively the "Export Laws").
 * Export of the Software may require some form of license or other
 * authority from the U.S. Government, and failure to obtain such
 * export control license may result in criminal liability under
 * U.S. laws. In addition, if the Software is identified as export controlled
 * items under the Export Laws, User represents and warrants that User
 * is not a citizen, or otherwise located within, an embargoed nation
 * (including without limitation Iran, Syria, Sudan, Cuba, and North Korea)
 *     and that User is not otherwise prohibited
 * under the Export Laws from receiving the Software.
 *
 * SUPPORT
 * GLIMPSE-CE is a derivative of the open-source USEPA GLIMPSE software.
 * For the GLIMPSE project, GCAM development, data processing, and support for 
 * policy implementations has been led by Dr. Steven J. Smith of PNNL, via Interagency 
 * Agreements 89-92423101 and 89-92549601. Contributors from PNNL include 
 * Maridee Weber, Catherine Ledna, Gokul Iyer, Page Kyle, Marshall Wise, Matthew 
 * Binsted, and Pralit Patel. 
 * The lead GLIMPSE & GLIMPSE- CE developer is Dr. Dan Loughlin (formerly USEPA). 
 * Contributors include Tai Wu (USEPA), Farid Alborzi (ORISE), and Aaron Parks and 
 * Yadong Xu of ARA through the EPA Environmental Modeling and Visualization 
 * Laboratory contract.
 */
package gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import glimpseUtil.StatusChecker;

/**
 * ExecutionThread manages the execution of background tasks using an ExecutorService and tracks their status.
 * <p>
 * Thread-safe for job submission and status checking. Provides methods to submit commands, runnables, and callables,
 * as well as to manage the executor's lifecycle. All access to the jobs list is synchronized for thread safety.
 * </p>
 */
public class ExecutionThread implements AutoCloseable {
    private ExecutorService executorService = null;
    /**
     * List of submitted jobs. All iteration over this list must be synchronized on the list.
     */
    private final List<Future<?>> jobs = Collections.synchronizedList(new ArrayList<>());
    private final StatusChecker status = new StatusChecker();
    private final AtomicBoolean isCheckingStatus = new AtomicBoolean(false);
    private int numDone = 0;

    /**
     * Checks if the number of completed jobs has changed since the last check.
     * <p>
     * <b>Thread safety:</b> This method is thread-safe. It synchronizes on the jobs list.
     * </p>
     *
     * @return true if the number of completed jobs has changed, false otherwise.
     */
    public boolean didNumDoneChange() {
        boolean rtnBool = false;
        int localNumDone = 0;
        synchronized (jobs) {
            for (Future<?> job : jobs) {
                if (job.isDone()) localNumDone++;
            }
        }
        if (localNumDone == numDone) {
            rtnBool = false;
        } else {
            rtnBool = true;
            numDone = localNumDone;
        }
        return rtnBool;
    }

    /**
     * Adds an array of command strings as RunnableCmds to the execution queue.
     * <p>
     * <b>Thread safety:</b> This method is thread-safe. It synchronizes on the jobs list.
     * </p>
     *
     * @param commands Array of command strings.
     */
    public void submitCommands(String[] commands) {
        try {
            submitCommandTasks(commands);
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while adding commands to execute queue.", e);
        }
    }

    /**
     * Starts a single-threaded executor if not already started.
     * <p>
     * <b>Thread safety:</b> This method is not thread-safe and should be called during initialization.
     * </p>
     */
    public void startUpExecutorSingle() {
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        } else {
            System.err.println("ExecutorService already started.");
        }
    }

    /**
     * Starts a cached thread pool executor if not already started.
     * <p>
     * <b>Thread safety:</b> This method is not thread-safe and should be called during initialization.
     * </p>
     */
    public void startUpExecutorMulti() {
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
        } else {
            System.err.println("ExecutorService already started.");
        }
    }

    /**
     * Submits an array of Runnable tasks to the executor.
     * <p>
     * <b>Thread safety:</b> This method is thread-safe. It synchronizes on the jobs list.
     * </p>
     *
     * @param runnables Array of Runnable tasks.
     */
    public void executeRunnables(Runnable[] runnables) {
        for (Runnable runnable : runnables) {
            executeRunnable(runnable);
        }
    }

    /**
     * Submits a single Runnable task to the executor.
     * <p>
     * <b>Thread safety:</b> This method is thread-safe. It synchronizes on the jobs list.
     * </p>
     *
     * @param runnable The Runnable task.
     */
    public void executeRunnable(Runnable runnable) {
        if (executorService == null) {
            throw new IllegalStateException("ExecutorService not started.");
        }
        startStatusCheckerIfNeeded();
        System.out.println("Submitting to queue: " + runnable);
        Future<?> f = executorService.submit(runnable);
        jobs.add(f);
    }

    /**
     * Submits an array of command strings as RunnableCmds to the executor.
     * <p>
     * <b>Thread safety:</b> This method is thread-safe. It synchronizes on the jobs list.
     * </p>
     *
     * @param commands Array of command strings.
     * @throws InterruptedException if interrupted while submitting tasks.
     */
    public void submitCommandTasks(String[] commands) throws InterruptedException {
        for (String command : commands) {
            submitCommand(command);
        }
    }

    /**
     * Submits a single command string as a RunnableCmd to the executor.
     * <p>
     * <b>Thread safety:</b> This method is thread-safe. It synchronizes on the jobs list.
     * </p>
     *
     * @param command The command string.
     * @return Future representing the submitted task.
     */
    public Future<?> submitCommand(String command) {
        if (executorService == null) {
            throw new IllegalStateException("ExecutorService not started.");
        }
        startStatusCheckerIfNeeded();
        RunnableCmd gr = new RunnableCmd();
        gr.setCmd(command);
        System.out.println("Submitting to queue: " + command);
        Future<?> f = executorService.submit(gr);
        synchronized (jobs) {
            jobs.add(f);
        }
        return f;
    }

    /**
     * Submits an array of command strings as RunnableCmds to the executor, specifying a working directory.
     * <p>
     * <b>Thread safety:</b> This method is thread-safe. It synchronizes on the jobs list.
     * </p>
     *
     * @param commands Array of command strings.
     * @param directory The working directory.
     * @throws InterruptedException if interrupted while submitting tasks.
     */
    public void submitCommandsWithDirectory(String[] commands, String directory) throws InterruptedException {
        for (String command : commands) {
            submitCommandWithDirectory(command, directory);
        }
    }

    /**
     * Submits a single command string as a RunnableCmd to the executor, specifying a working directory.
     * <p>
     * <b>Thread safety:</b> This method is thread-safe. It synchronizes on the jobs list.
     * </p>
     *
     * @param command The command string.
     * @param directory The working directory.
     * @return Future representing the submitted task.
     */
    public Future<?> submitCommandWithDirectory(String command, String directory) {
        if (executorService == null) {
            throw new IllegalStateException("ExecutorService not started.");
        }
        RunnableCmd gr = new RunnableCmd();
        gr.setCmd(command, directory);
        System.out.println("Submitting to queue: " + command + " with dir " + directory);
        Future<?> f = executorService.submit(gr);
        synchronized (jobs) {
            jobs.add(f);
        }
        return f;
    }

    /**
     * Submits a Callable task to the executor.
     * <p>
     * <b>Thread safety:</b> This method is thread-safe. It synchronizes on the jobs list.
     * </p>
     *
     * @param <V> The result type returned by the Callable.
     * @param callable The Callable task.
     */
    public <V> void executeCallableCmd(Callable<V> callable) {
        if (executorService == null) {
            throw new IllegalStateException("ExecutorService not started.");
        }
        startStatusCheckerIfNeeded();
        System.out.println("Submitting callable to queue: " + callable);
        Future<?> f = executorService.submit(callable);
        synchronized (jobs) {
            jobs.add(f);
        }
    }

    /**
     * Removes completed jobs from the jobs list to prevent memory leaks in long-running applications.
     * <p>
     * <b>Thread safety:</b> This method is thread-safe. It synchronizes on the jobs list.
     * </p>
     */
    public void cleanupCompletedJobs() {
        synchronized (jobs) {
            jobs.removeIf(Future::isDone);
        }
    }

    /**
     * Shuts down the executor service gracefully and terminates the status checker.
     * Logs any tasks that did not complete.
     * <p>
     * <b>Thread safety:</b> This method is thread-safe. It synchronizes on the jobs list and executor state.
     * </p>
     */
    @Override
    public void close() {
        shutdown();
    }

    /**
     * Shuts down the executor service gracefully and terminates the status checker.
     * Logs any tasks that did not complete.
     * <p>
     * <b>Thread safety:</b> This method is thread-safe. It synchronizes on the jobs list and executor state.
     * </p>
     */
    public void shutdown() {
        try {
            status.terminate();
        } finally {
            isCheckingStatus.set(false);
            if (executorService != null) {
                try {
                    executorService.shutdown();
                    if (!executorService.awaitTermination(30, java.util.concurrent.TimeUnit.SECONDS)) {
                        System.err.println("ExecutorService did not terminate in the specified time.");
                        List<Runnable> droppedTasks = executorService.shutdownNow();
                        if (!droppedTasks.isEmpty()) {
                            System.err.println("ExecutorService was abruptly shut down. The following tasks will not be executed:");
                            for (Runnable task : droppedTasks) {
                                System.err.println(task);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Shutdown interrupted. Remaining tasks may not have completed.");
                } catch (Exception e) {
                    throw new RuntimeException("Error during executorService shutdown.", e);
                } finally {
                    executorService = null;
                }
            }
        }
    }

    /**
     * Shuts down the executor service immediately and terminates the status checker.
     * Attempts to interrupt running tasks and logs any tasks that did not start.
     * <p>
     * <b>Thread safety:</b> This method is thread-safe. It synchronizes on the jobs list and executor state.
     * </p>
     */
    public void shutdownNow() {
        System.out.println("Attempting to shut down all model threads.");
        try {
            status.terminate();
        } finally {
            isCheckingStatus.set(false);
            if (executorService != null) {
                try {
                    List<Runnable> notStarted = executorService.shutdownNow();
                    if (!notStarted.isEmpty()) {
                        System.err.println("The following tasks were not started and will not be executed:");
                        for (Runnable task : notStarted) {
                            System.err.println(task);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error during executorService shutdownNow.", e);
                } finally {
                    executorService = null;
                }
            }
        }
    }

    /**
     * Checks if the executor service is currently executing tasks.
     * <p>
     * <b>Thread safety:</b> This method is thread-safe for concurrent reads.
     * </p>
     *
     * @return true if executing, false otherwise.
     */
    public boolean isExecuting() {
        if (executorService == null) {
            return false;
        } else {
            boolean isExecuting = !executorService.isShutdown();
            boolean isTerminated = !executorService.isTerminated();
            System.out.println("Is executing: " + isExecuting);
            System.out.println("Is terminated: " + isTerminated);
            return isExecuting;
        }
    }

    /**
     * Returns a string representation of the executor service queue.
     * <p>
     * <b>Thread safety:</b> This method is thread-safe for concurrent reads.
     * </p>
     *
     * @return String representation of the executor service.
     */
    public String getQueue() {
        return executorService != null ? executorService.toString() : "ExecutorService not started";
    }

    /**
     * Returns the list of jobs. All iteration over this list must be synchronized on the list.
     * <p>
     * <b>Thread safety:</b> This method is thread-safe. It returns a copy of the jobs list within a synchronized block.
     * </p>
     *
     * @return List of Future jobs.
     */
    public List<Future<?>> getJobs() {
        synchronized (jobs) {
            return new ArrayList<>(jobs);
        }
    }

    /**
     * Returns the status checker instance.
     * <p>
     * <b>Thread safety:</b> This method is thread-safe for concurrent reads.
     * </p>
     *
     * @return StatusChecker instance.
     */
    public StatusChecker getStatusChecker() {
        return status;
    }

    /**
     * Helper method to start the status checker if not already started.
     * This method is thread-safe.
     */
    private void startStatusCheckerIfNeeded() {
        if (isCheckingStatus.compareAndSet(false, true)) {
            status.start();
        }
    }

    // Deprecated methods for backward compatibility
    /**
     * @deprecated Use submitCommands instead.
     */
    @Deprecated
    public void addCommandsToQueue(String[] args) {
        submitCommands(args);
    }
    /**
     * @deprecated Use submitCommandTasks instead.
     */
    @Deprecated
    public void executeRunnableCmds(String[] args) throws InterruptedException {
        submitCommandTasks(args);
    }
    /**
     * @deprecated Use submitCommand instead.
     */
    @Deprecated
    public Future<?> executeRunnableCmd(String arg) {
        return submitCommand(arg);
    }
    

    
}