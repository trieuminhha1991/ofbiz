import com.olbius.salesmtl.DistributorServices;

Map<String, Object> supervisor = DistributorServices.getSupervisor(delegator, userLogin.partyId);
context.supervisor = supervisor;
context.representative = DistributorServices.getSupervisorRepresentative(delegator, dispatcher, userLogin, supervisor.supervisorId);