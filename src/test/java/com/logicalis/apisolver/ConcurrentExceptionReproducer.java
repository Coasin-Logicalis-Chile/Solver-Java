package com.logicalis.apisolver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class demonstrates how to reproduce concurrent exceptions in Spring Boot applications.
 * These examples show common scenarios that can occur in your Solver API application.
 */
@SpringBootTest
@ActiveProfiles("test")
public class ConcurrentExceptionReproducer {

    /**
     * Scenario 1: ConcurrentModificationException with ArrayList
     * This reproduces the vulnerability found in SnAttachmentController.java line 175-176
     * where ArrayList is used in a concurrent environment.
     */
    @Test
    public void reproduceConcurrentModificationException() {
        System.out.println("=== Reproducing ConcurrentModificationException ===");
        
        // This simulates the ArrayList usage in your controllers
        List<String> attachments = new ArrayList<>();
        
        // Fill the list with some data
        for (int i = 0; i < 1000; i++) {
            attachments.add("attachment_" + i);
        }
        
        // Create two threads that modify the same list concurrently
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger exceptionCount = new AtomicInteger(0);
        
        // Thread 1: Iterates through the list (like forEach in your code)
        executor.submit(() -> {
            try {
                for (String attachment : attachments) {
                    Thread.sleep(1); // Simulate processing time
                    // This can throw ConcurrentModificationException
                    System.out.println("Processing: " + attachment);
                }
            } catch (ConcurrentModificationException e) {
                exceptionCount.incrementAndGet();
                System.err.println("❌ ConcurrentModificationException caught: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            latch.countDown();
        });
        
        // Thread 2: Modifies the list while iteration is happening
        executor.submit(() -> {
            try {
                Thread.sleep(10); // Let iteration start first
                for (int i = 0; i < 100; i++) {
                    attachments.add("new_attachment_" + i);
                    attachments.remove(0); // This causes the exception
                    Thread.sleep(1);
                }
            } catch (Exception e) {
                System.err.println("Modification thread exception: " + e.getMessage());
            }
            latch.countDown();
        });
        
        try {
            latch.await(10, TimeUnit.SECONDS);
            System.out.println("✅ ConcurrentModificationException reproduced " + exceptionCount.get() + " times");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
    }

    /**
     * Scenario 2: Race Condition with Array Access
     * This reproduces the pattern found in your SnAttachmentController where arrays 
     * like `final Domain[] domain = new Domain[1]` are used across threads.
     */
    @Test
    public void reproduceRaceConditionWithArrays() {
        System.out.println("=== Reproducing Race Condition with Arrays ===");
        
        // This simulates the array pattern used in your controllers
        final String[] sharedData = new String[1];
        final AtomicInteger raceConditions = new AtomicInteger(0);
        
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(100);
        
        // Simulate 100 concurrent requests like in your API
        for (int i = 0; i < 100; i++) {
            final int requestId = i;
            executor.submit(() -> {
                try {
                    // Thread 1: Read and modify (like your forEach lambda)
                    sharedData[0] = "processing_request_" + requestId;
                    Thread.sleep(1); // Simulate processing
                    
                    // Thread 2: Check and use the data
                    String data = sharedData[0];
                    if (data != null && !data.equals("processing_request_" + requestId)) {
                        raceConditions.incrementAndGet();
                        System.err.println("❌ Race condition detected! Expected: processing_request_" 
                            + requestId + ", Found: " + data);
                    }
                } catch (Exception e) {
                    System.err.println("Exception in race condition test: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        try {
            latch.await(10, TimeUnit.SECONDS);
            System.out.println("✅ Race conditions detected: " + raceConditions.get() + " times");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
    }

    /**
     * Scenario 3: Deadlock Simulation
     * This simulates potential deadlocks that can occur when multiple threads 
     * access database services concurrently (like your service calls).
     */
    @Test
    public void reproduceDeadlockScenario() {
        System.out.println("=== Reproducing Deadlock Scenario ===");
        
        final Object lock1 = new Object();
        final Object lock2 = new Object();
        
        ExecutorService executor = Executors.newFixedThreadPool(2);
        AtomicInteger deadlockDetected = new AtomicInteger(0);
        
        // Thread 1: Acquires lock1 then lock2 (like attachmentService then incidentService)
        Future<?> task1 = executor.submit(() -> {
            try {
                synchronized (lock1) {
                    System.out.println("Thread 1: Acquired lock1 (AttachmentService)");
                    Thread.sleep(100);
                    
                    synchronized (lock2) {
                        System.out.println("Thread 1: Acquired lock2 (IncidentService)");
                        // Do work
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Thread 2: Acquires lock2 then lock1 (like incidentService then attachmentService)
        Future<?> task2 = executor.submit(() -> {
            try {
                Thread.sleep(50); // Small delay to ensure thread 1 starts first
                synchronized (lock2) {
                    System.out.println("Thread 2: Acquired lock2 (IncidentService)");
                    Thread.sleep(100);
                    
                    synchronized (lock1) {
                        System.out.println("Thread 2: Acquired lock1 (AttachmentService)");
                        // Do work
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        try {
            // Wait for completion with timeout to detect deadlock
            task1.get(5, TimeUnit.SECONDS);
            task2.get(5, TimeUnit.SECONDS);
            System.out.println("✅ Both threads completed successfully - No deadlock this time");
        } catch (TimeoutException e) {
            deadlockDetected.incrementAndGet();
            System.err.println("❌ Deadlock detected! Threads are waiting for each other");
            task1.cancel(true);
            task2.cancel(true);
        } catch (Exception e) {
            System.err.println("Other exception: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
        
        System.out.println("Deadlock scenarios detected: " + deadlockDetected.get());
    }

    /**
     * Scenario 4: Memory Consistency Errors
     * This reproduces issues where changes made by one thread might not be 
     * visible to other threads (like static variables).
     */
    @Test
    public void reproduceMemoryConsistencyError() {
        System.out.println("=== Reproducing Memory Consistency Errors ===");
        
        // This simulates non-volatile shared variables
        final int[] sharedCounter = {0};
        final boolean[] stopFlag = {false};
        
        ExecutorService executor = Executors.newFixedThreadPool(2);
        AtomicInteger inconsistencies = new AtomicInteger(0);
        
        // Writer thread
        Future<?> writer = executor.submit(() -> {
            for (int i = 0; i < 1000; i++) {
                sharedCounter[0] = i;
                if (i == 999) {
                    stopFlag[0] = true; // Signal to stop
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        
        // Reader thread
        Future<?> reader = executor.submit(() -> {
            int lastValue = -1;
            while (!stopFlag[0]) {
                int currentValue = sharedCounter[0];
                if (currentValue < lastValue) {
                    inconsistencies.incrementAndGet();
                    System.err.println("❌ Memory consistency error! Value went backwards: " 
                        + lastValue + " -> " + currentValue);
                }
                lastValue = currentValue;
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        
        try {
            writer.get(10, TimeUnit.SECONDS);
            reader.get(10, TimeUnit.SECONDS);
            System.out.println("✅ Memory consistency errors detected: " + inconsistencies.get());
        } catch (Exception e) {
            System.err.println("Exception in memory consistency test: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }

    /**
     * Scenario 5: HTTP Session Race Conditions
     * This simulates the race conditions that can occur with HTTP sessions
     * when multiple requests from the same user hit your API concurrently.
     */
    @Test
    public void reproduceHttpSessionRaceCondition() {
        System.out.println("=== Reproducing HTTP Session Race Conditions ===");
        
        // Simulate session data
        Map<String, Object> sessionData = new HashMap<>();
        AtomicInteger raceConditions = new AtomicInteger(0);
        
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(20);
        
        // Simulate 20 concurrent requests from the same user
        for (int i = 0; i < 20; i++) {
            final int requestId = i;
            executor.submit(() -> {
                try {
                    // Simulate reading and writing session data
                    Object currentValue = sessionData.get("userPreference");
                    Thread.sleep(1); // Simulate processing delay
                    
                    // Race condition: Another thread might have modified this
                    sessionData.put("userPreference", "request_" + requestId);
                    
                    Thread.sleep(1);
                    
                    Object newValue = sessionData.get("userPreference");
                    if (newValue != null && !newValue.equals("request_" + requestId)) {
                        raceConditions.incrementAndGet();
                        System.err.println("❌ Session race condition! Expected: request_" 
                            + requestId + ", Found: " + newValue);
                    }
                } catch (Exception e) {
                    System.err.println("Session test exception: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        try {
            latch.await(10, TimeUnit.SECONDS);
            System.out.println("✅ Session race conditions detected: " + raceConditions.get() + " times");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
    }

    /**
     * Main method to run all reproduction scenarios
     */
    public static void main(String[] args) {
        ConcurrentExceptionReproducer reproducer = new ConcurrentExceptionReproducer();
        
        System.out.println("🚀 Starting Concurrent Exception Reproduction Tests...\n");
        
        reproducer.reproduceConcurrentModificationException();
        System.out.println();
        
        reproducer.reproduceRaceConditionWithArrays();
        System.out.println();
        
        reproducer.reproduceDeadlockScenario();
        System.out.println();
        
        reproducer.reproduceMemoryConsistencyError();
        System.out.println();
        
        reproducer.reproduceHttpSessionRaceCondition();
        
        System.out.println("\n✅ All concurrent exception reproduction tests completed!");
        System.out.println("\n💡 To fix these issues in your application:");
        System.out.println("   1. Use ConcurrentHashMap instead of HashMap");
        System.out.println("   2. Use CopyOnWriteArrayList instead of ArrayList for concurrent reads");
        System.out.println("   3. Use synchronized blocks or locks for critical sections");
        System.out.println("   4. Use volatile keyword for shared variables");
        System.out.println("   5. Consider using ThreadLocal for per-thread data");
    }
}
