import org.gentsim.framework.EventDescription

event = new EventDescription("entity-state-changed")
event.attribute "entity_type"
event.attribute "entity"
event.attribute "changed_attributes", []
