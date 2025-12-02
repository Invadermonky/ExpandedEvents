## Expanded Events
Expanded Events adds a number of new event handlers to Minecraft. This mod is for developers who want to access these methods without needing to write mixins every time.

### New Events
- **[LivingSprintStartEvent](https://github.com/Invadermonky/ExpandedEvents/blob/master/src/main/java/com/expandedevents/events/LivingSprintStartEvent.java):** Fired whenever a living entity starts sprinting. Can be cancelled.
- **[LivingSprintStopEvent](https://github.com/Invadermonky/ExpandedEvents/blob/master/src/main/java/com/expandedevents/events/LivingSprintStopEvent.java):** Fired whenever a living entity stops sprinting. Can be cancelled.
- **[UpdateFoodStatsEvent](https://github.com/Invadermonky/ExpandedEvents/blob/master/src/main/java/com/expandedevents/events/UpdateFoodStatsEvent.java):** Fired whenever the player's food stats are updated. Can be used to cancel natural healing or reduce passive saturation drain.
