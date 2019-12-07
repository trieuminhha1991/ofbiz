
import java.util.*;

communicationEventTypes = delegator.findList("CommunicationEventType", null, null, null, null, false);
System.out.println("communication event type : " + communicationEventTypes);
context.communicationEventTypes = communicationEventTypes;