package test.Agents;

import java.util.Random;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import trasmapi.genAPI.exceptions.UnimplementedMethod;
import trasmapi.sumo.SumoCom;
import trasmapi.sumo.SumoVehicle;

public class DriverAgent extends Agent{

	public static Random rand;
	private int id;
	private String origin;
	private String destination;
	private SumoVehicle vehicle;
	protected boolean goingTripDone = false;
	protected boolean returnTripDone = false;


	public DriverAgent(int i) {
		super();


		this.id= i;

		origin = SumoCom.edgesIDs.get(rand.nextInt(SumoCom.edgesIDs.size()));

		do{
			destination = SumoCom.edgesIDs.get(rand.nextInt(SumoCom.edgesIDs.size())); 
		}while(origin.equals(destination));


		String vehicleType = SumoCom.vehicleTypesIDs.get(rand.nextInt(SumoCom.vehicleTypesIDs.size()));
		String routeId = SumoCom.getRouteId(origin, null);
		int departureTime = 0;
		double departPosition = 0;
		double departSpeed = 0;
		byte departLane = 0;


		vehicle = new SumoVehicle(id, vehicleType, routeId, departureTime, departPosition, departSpeed, departLane);

		SumoCom.addVehicle((SumoVehicle)vehicle);

		SumoCom.addVehicleToSimulation((SumoVehicle)vehicle);

		vehicle.changeTarget(destination);

	}





	@Override
	protected void setup() {

		DFAgentDescription ad = new DFAgentDescription();
		ad.setName(getAID()); //agentID
		System.out.println("AID: "+ad.getName());

		ServiceDescription sd = new ServiceDescription();
		sd.setName(getName()); //nome do agente    
		System.out.println("Nome: "+sd.getName());

		sd.setType("Driver");
		System.out.println("Tipo: "+sd.getType()+"\n\n\n");

		ad.addServices(sd); 

		try {
			DFService.register(this, ad);
		} catch(FIPAException e) {
			e.printStackTrace();
		}

		addBehaviour(new SimpleBehaviour() {

			private static final long serialVersionUID = 7099828445735670474L;

			public boolean done() {

				if(goingTripDone && returnTripDone ){
					System.out.println("END TRIP - "+getLocalName());
					return true;
				}
				return false;
			}

			@Override
			public void action() {

				if(vehicle.arrived)
				{
					if(!goingTripDone){

						System.out.println("CHEGUEI!! - "+getLocalName());
						goingTripDone = true;

						String temp = destination;
						destination = origin;
						origin = temp;

						vehicle.routeId = SumoCom.getRouteId(origin, null);
						vehicle.departTime = SumoCom.getCurrentSimStep();

						SumoCom.addVehicleToSimulation((SumoVehicle)vehicle);

						vehicle.arrived = false;

						vehicle.changeTarget(destination);


					}
					else
						returnTripDone = true;
				}
				block();

			}
		});


	}

}
