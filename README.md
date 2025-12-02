## Expanded Events
Expanded Events adds a number of new event handlers to Minecraft. This mod is for developers who want to access these methods without needing to write mixins every time.

### Added Events
- **UpdateFoodStatsEvent:** Fired whenever the player's food stats are updated. Can be used to cancel natural healing or reduce passive saturation drain.
- **LivingSprintStartEvent:** Fired whenever a living entity starts sprinting. Can be canceled.
- **LivingSprintStopEvent:** Fired whenever a living entity stops sprinting. Event listener only.
