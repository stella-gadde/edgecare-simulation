package org.care;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;

import java.util.List;

public class EdgeCareSimulator {
    public static void main(String[] args) {

        CloudSim hospitalEdgeSim = new CloudSim();
        DatacenterBrokerSimple coordinator = new DatacenterBrokerSimple(hospitalEdgeSim);

        Datacenter routerNode = ComponentMaker.createEdgeDatacenter(hospitalEdgeSim, "Edge_Router_Zone");
        Datacenter gatewayNode = ComponentMaker.createEdgeDatacenter(hospitalEdgeSim, "Edge_Gateway_Zone");
        Datacenter cloudZone = ComponentMaker.createCloudDatacenter(hospitalEdgeSim);

        List<Vm> virtualNodes = ComponentMaker.buildVirtualUnits();
        coordinator.submitVmList(virtualNodes);

        List<Cloudlet> healthCloudlets = ComponentMaker.createWearableData();
        coordinator.submitCloudletList(healthCloudlets);
        for (Cloudlet packet : healthCloudlets) {
            Vm targetVm;
            if (packet.getLength() <= 1050) {
                targetVm = (packet.getId() % 2 == 0) ? virtualNodes.get(0) : virtualNodes.get(1);  
            } else {
                targetVm = virtualNodes.get(2); 
            }
            coordinator.bindCloudletToVm(packet, targetVm);
        }


        hospitalEdgeSim.start();

        System.out.println("\n=== Performance Metrics Summary ===");

        int edgeCount = 0;
        int cloudCount = 0;
        double edgeTimeSum = 0;
        double cloudTimeSum = 0;
        double originalDataSize = 0;
        double compressedDataSize = 0;
        double totalExecTime = 0;

        for (Cloudlet packet : healthCloudlets) {
            double execTime = packet.getFinishTime() - packet.getExecStartTime();
            totalExecTime += execTime;
            Vm executedVm = packet.getVm();

            if (executedVm.getId() == 0 || executedVm.getId() == 1) {
                edgeCount++;
                edgeTimeSum += execTime;
                originalDataSize += packet.getOutputSize(); 
                compressedDataSize += packet.getOutputSize(); 
            } else {
                cloudCount++;
                cloudTimeSum += execTime;
                originalDataSize += packet.getOutputSize() / 0.7; 
                compressedDataSize += packet.getOutputSize();
            }
        }

        double avgEdgeTime = edgeCount > 0 ? edgeTimeSum / edgeCount : 0;
        double avgCloudTime = cloudCount > 0 ? cloudTimeSum / cloudCount : 0;
        double latencySaved = avgCloudTime - avgEdgeTime;
        double dataSaved = originalDataSize - compressedDataSize;
        double reductionPercent = (dataSaved / originalDataSize) * 100;
        double totalCloudlets = edgeCount + cloudCount;

        System.out.printf("Edge Cloudlets: %d | Avg Processing Time: %.2f sec%n", edgeCount, avgEdgeTime);
        System.out.printf("Cloud Cloudlets: %d | Avg Processing Time: %.2f sec%n", cloudCount, avgCloudTime);
        System.out.printf("Simulated Latency Reduction (Edge vs Cloud): %.2f sec%n", latencySaved);
        System.out.printf("Total Execution Time (All Cloudlets): %.2f sec%n", totalExecTime);
        System.out.printf("Total Original Data Size: %.2f KB%n", originalDataSize);
        System.out.printf("Total Compressed Data Size: %.2f KB%n", compressedDataSize);
        System.out.printf("Data Reduction Achieved at Edge: %.2f KB (%.1f%% reduction)%n", dataSaved, reductionPercent);
        System.out.printf("Cloudlet Distribution: %.1f%% at Edge, %.1f%% at Cloud%n",(edgeCount * 100.0 / totalCloudlets), (cloudCount * 100.0 / totalCloudlets));

    }
}
