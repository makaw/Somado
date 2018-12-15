/*
 * 
 *  Somado (System Optymalizacji Małych Dostaw)
 *  Optymalizacja dostaw towarów, dane OSM, problem VRP
 * 
 *  Autor: Maciej Kawecki 2016 (praca inż. EE PW)
 * 
 */
package spatial_vrp;

import datamodel.Order;
import datamodel.Pack;
import gui.GUI;
import gui.loader.IProgress;
import gui.loader.IProgressInvoker;
import gui.loader.Loader;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.SchrimpfFactory;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter.Print;
import com.graphhopper.jsprit.core.util.Solutions;
import somado.AppObserver;
import somado.IConf;
import somado.Lang;
import somado.Settings;


/**
 *
 * Rozwiązywanie problemu VRP
 * 
 * @author Maciej Kawecki
 * @version 1.0
 * 
 */
public class VRPSolver implements IProgressInvoker {
 
    
  /** Lista paczek (zamówień pogrupowanych wg punktu dostawy) */
  private List<Pack> packs;  
  /** Macierz kosztów dla punktów dostawy */
  private final CostMatrix costMatrix;
  /** Lista wybranych kierowców */
  private final List<datamodel.Driver> drivers;
  /** Problem VRP */
  private VehicleRoutingProblem problem;
  /** Najlepsze wyznaczone rozwiązanie problemu VRP */
  private VehicleRoutingProblemSolution bestSolution;
  /** Maksymalny czas przejazdu kierowcy w jednej dostawie */
  private double maxDriverWorkTime;
  
  
  /**
   * Konstruktor
   * @param frame Ref. do GUI 
   * @param costMatrix Macierz kosztów dla punktów dostawy
   * @param drivers Lista wybranych kierowców
   */
  public VRPSolver(GUI frame, CostMatrix costMatrix, List<datamodel.Driver> drivers) {
       
     this.costMatrix = costMatrix;
     this.drivers = drivers;
     
     maxDriverWorkTime = IConf.DEFAULT_MAX_DRIVER_WORK_TIME;
     try {
       maxDriverWorkTime = Double.valueOf(Settings.getValue("driver_max_work_time"));
     }
     catch (NumberFormatException e) {}
     maxDriverWorkTime *= 3600.0;
     
     new Loader(frame, Lang.get("Dialogs.Progress.VRP"), this, false).load();   
      
  }  
  
  
  /**
   * Rozwiązanie  problemu VRP (algorytm Schrimpfa, biblioteka JSprit)
   */
  private void solveVRP() {      
      
    
    // przestawienie standardowego wyjścia na plik z logami VRP
    PrintStream stdout = System.out;
    PrintStream stderr = System.err;
    PrintStream ps = stdout;
   
    try {       
    	
      ps = new PrintStream(new BufferedOutputStream(new FileOutputStream("somado_vrp.log")));
      System.setOut(ps);
      System.setErr(ps);
       
    } catch (IOException e) {
       System.err.println(e);
    } 
    
   
    AppObserver observer = new AppObserver();
    observer.addObserver(costMatrix);
    
    // definiowanie problemu VRP
    VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
    vrpBuilder.setFleetSize(FleetSize.FINITE);
    vrpBuilder.setRoutingCost(costMatrix);
    
    // zdefiniowanie pojazdów
    Iterator<datamodel.Driver> driverIterator = drivers.iterator();
    while (driverIterator.hasNext()) {
              
       datamodel.Driver tmp = driverIterator.next(); 
        
       // typ pojazdu, ustawienie ładowności i średniego zużycia paliwa
       VehicleTypeImpl vehicleType = VehicleTypeImpl.Builder.newInstance(String.valueOf(tmp.getVehicle().getVehicleModel().getId()))
           .addCapacityDimension(0, (int) (tmp.getVehicle().getVehicleModel().getMaximumLoad()*1000.0))
           .setCostPerDistance(tmp.getVehicle().getVehicleModel().getAvgFuelConsumption()).build();
       
       // konkretny pojazd, start w magazynie, powrót lub nie, ustawienie maksymalnego czasu przejazdu
       VehicleImpl vehicle = VehicleImpl.Builder.newInstance(String.valueOf(tmp.getVehicle().getId()))
               .setStartLocation(Location.newInstance(costMatrix.getDepotIndex())).setReturnToDepot(tmp.isReturnToDepot())
               .setLatestArrival(maxDriverWorkTime).setType(vehicleType).build();
       
         
        vrpBuilder = vrpBuilder.addVehicle(vehicle);   

       
    }
    
    // zdefiniowanie zadań (punktów dostawy)
    packs = costMatrix.getPacksList();
    Iterator<Pack> packIterator = packs.iterator();
    while (packIterator.hasNext()) {
        
       Pack tmp = packIterator.next();
       if (tmp.getCustomer().getIndex() == costMatrix.getDepotIndex()) continue;
       
       // ustawienie każdego cząstkowego zamówienia jako osobnego zadania
       List<Order> orders = tmp.getOrdersList();
       Iterator<Order> orderIterator = orders.iterator();
       while (orderIterator.hasNext()) {
           
         Order orderTmp = orderIterator.next();
         
         Service service = Service.Builder.newInstance(String.valueOf(orderTmp.getId()))
                 .addSizeDimension(0, (int) orderTmp.getTotalWeight())
                 .setLocation(Location.newInstance(tmp.getCustomer().getIndex())).build();         
         
         vrpBuilder.addJob(service);
         
       }
                      
    }    
   
  
    problem = vrpBuilder.build();          
        
    VehicleRoutingAlgorithm algorithm = new SchrimpfFactory().createAlgorithm(problem);
        
    // listener algorytmu dla macierzy kosztów
    algorithm.addListener(new VRPListener(observer));
    
    // znalezienie rozwiązania
    Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
    bestSolution = Solutions.bestOf(solutions);     
    
    // wydruk rozwiązania do logów
    SolutionPrinter.print(problem, bestSolution, Print.VERBOSE);
    
    ps.flush();
    
    // przywrócenie standardowego wyjścia 
    System.setOut(stdout);
    System.setErr(stderr);
      
  }
  
  
  public VehicleRoutingProblemSolution getBestSolution() {
      return bestSolution;
  }    

  
  public double getMaxDriverWorkTime() {
      return maxDriverWorkTime;
  }
  
  
  
  
           
    
  @Override
  public void start(IProgress progress) {
      
    solveVRP();
        
    progress.hideComponent();   
            
      
  }
  
  

}
