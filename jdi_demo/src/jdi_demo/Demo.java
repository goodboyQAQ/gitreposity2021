package jdi_demo;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachineManager;

public class Demo {
	public static void main(String[] args) {
		VirtualMachineManager virtualMachineManager = Bootstrap.virtualMachineManager();
		System.out.println(virtualMachineManager);
	}

}
