# POS Offline-First Android System

This project implements a robust offline-first POS system for mobile/tablet devices. It ensures smooth operation under limited connectivity, bidrectional syncing, and printing tasks with retry and conflict resolution.

### Table of Contents
* Overview
* Architecture
* Modules
  * Print Queue Manager
  * Event Bus
  * Conflict Resolution
  * Sync Repository
  * Workers
  * Retention ( Data Pruning)
* Usage
* DI

### Overview
This POS system handles:
* Offline-first order and inventory management
* Automatic synchronization between Local DB and remote server
* Print Queue Management for multiple printers( kichen, bar, billing, etc...)
* Conflict resolution for concurrent updates
* Retention of old data to manage storage limitations

It is built with
* Kotlin + Android
* Room for local persistance
* Work Manager for background jobs
* Hilt for DI
* Coroutines for Async Programming

### Architecture
* Data Layer : Handles data insertion, fetching, and event triggering
* PrintQueueManager: Processing all print jobs, handles retries, and printer availability
* WorkManager: Handles background tasks such as sync, fetch, print, and data retention.

### Modules
Print Queue Manager
Class: PrintQueueJobManager
* Manages all printer jobs (kitchen, billing, bar).
* Subscribes to events:
  * PrintJobCreatedEvent → Enqueues job
  * PrinterReadyEvent → Processes pending jobs
  * PrintJobCompletedEvent → Deletes completed jobs
* Handles retry logic with exponential backoff.
* Coroutine-based asynchronous processing.
  
#### Key Methods
* ```enqueue(job: PrintJob)``` -> Add a job to the queue
* ```processJobById(jobId: String)``` -> Process a specific job
* ```processPendingJobs()``` -> Process all pending jobs
* ```deleteCompletedPrintJob()``` -> Cleanup

### Event Bus
**Class**: EventBus
* Provides publisher-subscriber pattern using kotlin flows
* supports type-safe subscriptions
* Handles printing and sync states asynchronously
Usage Example
```
eventBus.post(PrintJobCreatedEvent(printJob))
eventBus.subscribe<PrintJobCompletedEvent>(scope) { event -> ... }

```

### Conflict Resolution
Class: ResolveConflictUseCase
* Resolves conflicts for Orders and inventory using last updated timestamp
* Ensures data consistency across devices
Example
```
fun resolveOrder(local: Order, remote: Order): Order {
    return if (remote.updatedAt > local.updatedAt) remote else local
}

fun resolveInventory(local: Inventory, remote: Inventory): Inventory {
    return if (remote.updatedAt > local.updatedAt) remote else local
}
```

### Sync Repository
**Interface**: ```SyncRepository```
* Handles fetching, pushing, and upserting orders and inventory.
* Works with offline DB and remote server.
* Provides per-item conflict resolution during sync.

#### Implementation: SyncRepositoryImpl
* ```getPendingChanges()``` -> Get unsynced local changes
* ```pushChange(changeRecord: ChangeRecord)``` -> Push a single change to remote
* ```markChangeSuccess / markChangeFailed``` -> Track status
* ```fetchRemoteOrders / fetchRemoteInventory``` -> Pull remote updates

### Workers
##### 1. PushSyncWorker
* Pushes pending local changes to server.
* Processes in batches of 20 for efficiency.
* Marks success/failure per-item.
* Retry is handled by WorkManager.

##### 2. FetchSyncWorker
* Pulls remote orders and inventory since last sync.
* Resolves conflicts with ResolveConflictUseCase.
* Updates LastSyncStore timestamp after successful fetch.

##### 3. PrintWorker
* Processes one-time or pending print jobs.
* Works with PrintQueueJobManager.
* Supports job-specific ID or all pending jobs.

##### 4. RetentionWorker
* Cleans up old data to manage storage:
  * Completed print jobs older than 7 days
  * Failed print jobs older than 30 days
  * Successful change records older than 7 days

### Usage
* Initialize DI with Hilt.
* Schedule Workers:
```PushSyncWorker.enqueueWork(context)```
* Post events to EventBus:
```eventBus.post(PrintJobCreatedEvent(printJob))```
* PrintQueueJobManager automatically handles processing and retries.

### Dependencies & DI
* Hilt/Dagger for dependency injection
* Room for offline database
* WorkManager for background tasks
* Gson for JSON serialization
* Kotlin Coroutines

#### DI Wiring Example:
```
@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val syncOfflineDataSource: SyncOfflineDataSource,
    private val syncRemoteDataSource: SyncRemoteDataSource,
    private val conflictUseCase: ResolveConflictUseCase
) : SyncRepository
```
