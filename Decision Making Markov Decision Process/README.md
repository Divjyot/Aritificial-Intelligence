# Markov Decision Process 
## This project undertakes a decision making problem and frame it as a markov decision process (MDP).

## Challenge:
A racing game format where the emphasize is on the strategy that a team should take — that is, deciding which driver, car models, and car components should be used when and where—, so that the team can reach the goal within a given number of steps. A car operates in an environment with varying terrain types. There are three variables that influence terrain types: dirt/asphalt, straight/slalom, and hilly/flat. The environment map, including the terrain types, is known perfectly and is represented as 1-dimensional grid cell of size N, where column-1 is the starting point and column-N is the goal region.

At each step, the team might be able to do one of the following:

### A1. Continue moving. 
  Depending on the terrain, driver, car models, and car components, each movement might move the car k cells, where 𝑘 ∈   −4, 5 ∪ 𝑠𝑙𝑖𝑝, 𝑏𝑟𝑒𝑎𝑘𝑑𝑜𝑤𝑛 . The result of the exact movement is uncertain and represented as a probability distribution. This distribution is conditioned on the components discussed in action #2—#6 below. To simplify computation of transition probability, this game assumes conditional independence of the parameters below (i.e., car type, driver, and tire model, given k) and prior distributions of the parameters and of k is assumed to be uniform. If the movement causes the car to have non-zero probability mass of being in cell index > N, then all probability mass for cells beyond N will be added to the probability mass of being at cell-N. The same is applied to cell-1.
The car might also slip, which will incur a substantial time before it can start moving again. The car might also break down, which will incur a repair time.

### A2. Change the car type. 
  Different type will have different speed in different terrains and different fuel efficiency. After the change, the car fuel is at full tank (50 liters) and the pressure of all of its tires are at 100% capacity.

### A3. Change the driver.
 Different drivers will have different speed in different terrains.

### A4. Change the tire(s) of existing car.
  The team has an option to change its entire tires with another model. The available tire types are: all-terrain, mud, low- profile, performance. The different types of tires and terrains will influence how far the car will move in a single step. After the change, the pressure of all its tires is at 100% capacity.

### A5. Add fuel to existing car.
  The car fuel level is an integer, with range 0 (empty tank) to 50 liters (full tank). The time it takes to fill up the fuel by x amount is ceil(x/10) steps. For example: If 8 liters is added, at the next step the agent can do something else, but if 19 liters is added, only at the next next step, the agent can choose another different action.

### A6. Change pressure to the tires. 
  The team can add/reduce the pressure to 50%
capacity, 75% capacity, or 100% capacity. The fuel consumption when the pressure is at 75% capacity is double that of 100% capacity. The fuel consumption when pressure is at 50% capacity is triple that of 100% capacity. However, the chances of slip at 75% capacity is double that of 50% capacity, and the chances of slip at 100% capacity is triple that of 50% capacity.

### A7. Combination of options A2 and A3.
### A8. CombinationofoptionsA4—A6.
The time to do the change follows the slowest step (i.e., if fuel is added, this will be fuel addition). 

## The game has 5 levels, which differ in the possible terrain types and available actions, as follows:
### Level-1: 
Only two types of terrain (i.e., dirt and asphalt) with N ≤ 10, and only actions A1—A4 are possible, with two different car models and two different drivers to choose from. Fuel and tire pressure are assumed to remain the same throughout the race.
### Level-2:
Only four types of terrain (i.e., dirt-straight, dirt-slalom, asphalt-straight, and asphalt-slalom) with N ≤ 10, and only actions A1—A6 are possible, with three different car models and two different drivers to choose from.

### Level-3: 
All eight terrain types (i.e., dirt-straight-hilly, dirt-straight-flat, dirt-slalom- hilly, dirt-slalom-flat, asphalt-straight-hilly, asphalt-straight-flat, asphalt- slalom-hilly, and asphalt-slalom-flat) with N ≤ 30, and only actions A1—A6 are possible, with five different car models and five different drivers to choose from.

### Level-4:
All eight terrain types with N ≤ 30, and actions A1—A7 are possible, with five different car models and five different drivers to choose from.

### Level-5:
All eight terrain types with N ≤ 30, and all eight classes of actions are possible, with five different car models and five different drivers to choose from.

## Solution
I tried to solve this MDP problem with the use of UCT or otherwise known as Monte Carlo Tree Search (MCTS) combined with UCB.
The following states pesudocode for overall algorithm:

![alt text] ()


(This project was a part of ARTIFICIAL INTELLIGENCE course)
