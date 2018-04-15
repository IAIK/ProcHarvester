package com.procharvester.Logging;

public class Config {

    public static final String[] RECURSIVE_TARGETS = {
            //"/proc", // Too slow, use intended for an overview of readable files
            "/proc/net",
    };

    public static final String[] FILES_TO_SKIP = {
            "/proc/net/unix", // Way too much entries
    };

    public static final LogTarget[] LOG_TARGETS = {

            /** VM Subsystem **/
            /** proc/vmstat and /proc/zoneinfo are restricted as of Android 8, use /proc/meminfo or /proc/zoneinfo instead
            new LogTarget("/proc/vmstat", null, null).setMultipleSubTargets(new LogTarget.SubTarget[]{
                    new LogTarget.SubTarget("nr_free_pages", 1), // new LogTarget("/proc/meminfo", "MemFree", 1), new LogTarget("/proc/zoneinfo", "pages free", 3),
                    new LogTarget.SubTarget("nr_anon_pages", 1), // new LogTarget("/proc/vmstat", "nr_active_anon", 1), new LogTarget("/proc/zoneinfo", "nr_anon_pages", 2), new LogTarget("/proc/meminfo", "AnonPages", 1), new LogTarget("/proc/meminfo", "Active(anon)", 1), new LogTarget("/proc/meminfo", "Active", 1),
                    new LogTarget.SubTarget("nr_mapped", 1), // new LogTarget("/proc/zoneinfo", "nr_inactive_anon", 2), new LogTarget("/proc/meminfo", "Mapped", 1),
                    new LogTarget.SubTarget("nr_shmem", 1), // new LogTarget("/proc/zoneinfo", "nr_shmem", 2), new LogTarget("/proc/meminfo", "Shmem", 1),
                    new LogTarget.SubTarget("nr_dirty_threshold", 1),
                    new LogTarget.SubTarget("pgalloc_dma", 1),
                    new LogTarget.SubTarget("pgfault", 1),
            }), **/

            // Works also for Android 8
            new LogTarget("/proc/meminfo", null, null).setMultipleSubTargets(new LogTarget.SubTarget[]{
                    new LogTarget.SubTarget("Mapped", 1),
                    new LogTarget.SubTarget("MemFree", 1),
                    new LogTarget.SubTarget("AnonPages", 1), // new LogTarget("/proc/meminfo", "AnonPages", 1), new LogTarget("/proc/meminfo", "Active", 1),
                    new LogTarget.SubTarget("Shmem", 1),
                    new LogTarget.SubTarget("Active(anon)", 1),
                    new LogTarget.SubTarget("Committed_AS", 1),
            }),


            /** Interrupts **/
            /** /proc/interrupts restricted as of Android 8
            new LogTarget("/proc/interrupts", null, null).setMultipleSubTargets(new LogTarget.SubTarget[] {
                    new LogTarget.SubTarget("arch_timer"),
                    new LogTarget.SubTarget("kgsl-3d0"),
                    new LogTarget.SubTarget("MDSS"),
                    new LogTarget.SubTarget("qcom,cpu-bwmon"),
                    // new LogTarget.SubTarget("wlan_pci"), only on One Plus 3T
                    new LogTarget.SubTarget(" dwc3"), // space is important to distinguish from msm_dwc3
                    // new LogTarget.SubTarget("ufshcd"), only on One Plus 3T
                    new LogTarget.SubTarget("Rescheduling interrupts"),
                    new LogTarget.SubTarget("Single function call interrupts"),
            }).setColumnCntLimit(4), **/

            /** /proc/stat restricted as of Android 8
            new LogTarget("/proc/stat", null, null).setMultipleSubTargets(new LogTarget.SubTarget[]{
                    new LogTarget.SubTarget("intr", 22),
                    new LogTarget.SubTarget("intr", 67),
                    new LogTarget.SubTarget("intr", 117),
                    new LogTarget.SubTarget("intr", 1),
                    new LogTarget.SubTarget("intr", 306),
                    // new LogTarget.SubTarget("softirq", 4),
                    // new LogTarget.SubTarget("softirq", 5),
            }), **/

            /** Network **/
            /* new LogTarget("/proc/net/dev", null, null).setMultipleSubTargets( new LogTarget.SubTarget[] {
                    new LogTarget.SubTarget("wlan0:"),
                    new LogTarget.SubTarget("wlan0:", 3),
                    new LogTarget.SubTarget("wlan0:", 10),
                    new LogTarget.SubTarget("wlan0:", 11),
            }),
            new LogTarget("/proc/net/sockstat", "sockets: used", 2), */

            /** Device dependent, touch screen interrupts for unlock pattern inference, 2016-oakland-interrupt **/
            // new LogTarget("/proc/interrupts", "clearpad", 2).setPermanentMode(),
    };

    public static final LogTarget[] WHOLE_FILE_TARGETS = {

            new LogTarget("/proc/sys/net/ipv4/netfilter/ip_conntrack_count", null, null),
            new LogTarget("/proc/sys/net/netfilter/nf_conntrack_count", null, null),
            /* Evaluated
            new LogTarget("/proc/interrupts", null, null),
            * */

            /* Evaluated
            new LogTarget("/proc/vmstat", null, null),
            new LogTarget("/proc/zoneinfo", null, null),
            new LogTarget("/proc/meminfo", null, null), */


            /* Evaluated: new LogTarget("/proc/buddyinfo", null, null),
            new LogTarget("/proc/cgroups", null, null),
            new LogTarget("/proc/cpuinfo", null, null),*/

            /* Evaluated: new LogTarget("/proc/schedstat", null, null),
            new LogTarget("/proc/loadavg", null, null), // parts in float
            new LogTarget("/proc/pagetypeinfo", null, null), */


            /* Evaluated new LogTarget("/proc/diskstats", null, null),
            new LogTarget("/proc/locks", null, null),
            new LogTarget("/proc/meminfo", null, null),
            new LogTarget("/proc/misc", null, null),
            new LogTarget("/proc/partitions", null, null),
            new LogTarget("/proc/swaps", null, null), */

            /** Evaluated new LogTarget("/proc/softirqs", null, null),
            new LogTarget("/proc/stat", null, null), **/

            /* Evaluated
            new LogTarget("/proc/sys/fs/inode-state", null, null),
            new LogTarget("/proc/net/sockstat", null, null),
            new LogTarget("/proc/net/dev", null, null),
            new LogTarget("/proc/net/netstat", null, null),
             */

            // Mentioned: /proc/net/tcp, /proc/net/tcp6
    };

    public static final int MAX_EVENT_SIZE = 5000;
    public static final int PREFETCH_BUFFER_SIZE = 2000;
    public static final long EVENT_TIME_OUT = 4000; // in milliseconds
    public static final long MAX_PREFETCH_TIME = 0; // in milliseconds

    public static final LogTarget[] WEBSITE_SIDE_CHANNELS = {
            /** VM Subsystem **/
            new LogTarget("/proc/vmstat", null, null).setMultipleSubTargets(new LogTarget.SubTarget[]{
                    new LogTarget.SubTarget("nr_free_pages", 1),
                    new LogTarget.SubTarget("nr_anon_pages", 1),
                    new LogTarget.SubTarget("nr_mapped", 1),
                    new LogTarget.SubTarget("nr_shmem", 1),
            }),

            /** Interrupts **/
            new LogTarget("/proc/interrupts", null, null).setMultipleSubTargets(new LogTarget.SubTarget[] {
                    new LogTarget.SubTarget("arch_timer"),
                    new LogTarget.SubTarget("kgsl-3d0"),
                    new LogTarget.SubTarget("Rescheduling interrupts"),
            }).setColumnCntLimit(4),


            new LogTarget("/proc/net/sockstat", "sockets: used", 2),
            new LogTarget("/proc/net/dev", "wlan0:", 10),
            new LogTarget("/proc/net/dev", "wlan0:", 11),
            new LogTarget("/proc/net/dev", "wlan0:", 2),
            new LogTarget("/proc/net/dev", "wlan0:", 3),

            new LogTarget("/proc/net/netstat", "IpExt: 0", 7),
            new LogTarget("/proc/net/netstat", "IpExt: 0", 14),
            new LogTarget("/proc/net/netstat", "IpExt: 0", 8),
    };
}
