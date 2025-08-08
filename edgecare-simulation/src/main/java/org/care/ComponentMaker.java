package org.care;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;

import java.util.ArrayList;
import java.util.List;

public class ComponentMaker {

    public static Datacenter createEdgeDatacenter(CloudSim sim, String label) {
        List<Pe> processingElements = new ArrayList<>();
        processingElements.add(new PeSimple(1000));

        Host microHost = new HostSimple(8192, 30000, 1000000, processingElements);
        microHost.setVmScheduler(new VmSchedulerTimeShared());

        Host extraHost = new HostSimple(8192, 30000, 1000000, processingElements);
        extraHost.setVmScheduler(new VmSchedulerTimeShared());

        List<Host> hosts = new ArrayList<>();
        hosts.add(microHost);
        hosts.add(extraHost);

        return new DatacenterSimple(sim, hosts, new VmAllocationPolicySimple());
    }

    public static Datacenter createCloudDatacenter(CloudSim sim) {
        List<Pe> cloudPEs = new ArrayList<>();
        cloudPEs.add(new PeSimple(2000));

        Host cloudHost = new HostSimple(16384, 60000, 1000000, cloudPEs);
        cloudHost.setVmScheduler(new VmSchedulerTimeShared());

        Host extraHost = new HostSimple(8192, 30000, 1000000, cloudPEs);
        extraHost.setVmScheduler(new VmSchedulerTimeShared());

        List<Host> cloudHosts = new ArrayList<>();
        cloudHosts.add(cloudHost);
        cloudHosts.add(extraHost);

        return new DatacenterSimple(sim, cloudHosts, new VmAllocationPolicySimple());
    }

    public static List<Vm> buildVirtualUnits() {
        List<Vm> vmList = new ArrayList<>();

        Vm routerVM = new VmSimple(0, 1000, 1);
        routerVM.setRam(1024).setBw(5000).setSize(10000);  

        Vm gatewayVM = new VmSimple(1, 1200, 1);
        gatewayVM.setRam(2048).setBw(8000).setSize(15000); 

        Vm cloudVM = new VmSimple(2, 2000, 1);
        cloudVM.setRam(4096).setBw(20000).setSize(50000);  

        vmList.add(routerVM);
        vmList.add(gatewayVM);
        vmList.add(cloudVM);

        return vmList;
    }

    public static List<Cloudlet> createWearableData() {
        List<Cloudlet> cloudlets = new ArrayList<>();
        UtilizationModelDynamic cpuUsage = new UtilizationModelDynamic(0.5);
        UtilizationModelDynamic ramUsage = new UtilizationModelDynamic(0.5);
        UtilizationModelDynamic bwUsage = new UtilizationModelDynamic(0.5);

        for (int i = 0; i < 6; i++) {
            CloudletSimple dataPacket = new CloudletSimple(1000 + i * 50, 1, cpuUsage);
            dataPacket.setFileSize(300 + i * 10);
            dataPacket.setOutputSize(300 + i * 10);
            dataPacket.setId(i);
            dataPacket.setUtilizationModelRam(ramUsage);
            dataPacket.setUtilizationModelBw(bwUsage);

            cloudlets.add(dataPacket);
        }

        return cloudlets;
    }

}
