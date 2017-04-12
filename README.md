# Robot Warehouse Assignment

### Description

This was one of my first year projects in robotics, where we had to simulate a robot warehouse system using 3 NXT robots. The goal was to make the robots to pick up items and deliver them to a drop off point in a grid map. All 3 robots had to move in the same time, synchronized. We built the system in a team of 6, everybody had a part of the assignment to do.

### Team member roles

Kimberly Amador - Robot interface
Vainas Brazdeikis - Warehouse interface and Network communication
Thomas Murphy - Motion control and Route execution
Jerome Atkins - Job input, selection and cancellation
Eligh John - System integration


### My contribution to the project

I was working on the multi-robot path planning, the localization of the robot, the travelling salesman problem and also helped in integrating all my parts in the big system. I will describe how my parts work below.

### Multi-robot path planning

I used the Windowed Hierarchical Cooperative A* (WHCA*) algorithm, which was planning for all the 3 robots simultaneously, respecting a 3 time-step window. After each 3 time steps the route was replanned. If, sometimes, no route was found for all 3 robots, some of the robots were staying in a place, giving priority to others (cooperation).

### TSP

I coded the Travelling Salesmen Problem (TSP) algorithm in order to assign the best possible job-picking route to each robot. This way the job picking and drop-off was sped up. The algorithm used simmulated annealing in order to find the TSP solution. This type of algorithm was chosen in order to make sure that it will not get stuck in a local optima and hopefully, after 200 iterations it will find the global optima. I used 200 iterations because the number of pick-up locations was small (6) and after 200 iterations the algorithm was finding the right solution.

### Localization

This algorithm was an extra for the project. It helped the robot, that if it was placed somewhere or got lost somehow, it was able to localize where it is in the maze after 3 to 5 steps. It worked using Monte Carlo Localization with Particle Filtering. In the algorithm, for each empty cell was assigned a particle. Then, after one step (FORWARD/BACKWARD/RIGHT/LEFT) it filtered the particles. The second filtering was based on the surroundings. The robot was sensing where it has walls next to it (FRONT/BACK/LEFT/RIGHT) and it was filtering the particles again based on those metrics. These two filtering processes kept looping until the algorithm converged and the robots right location was returned.

### Video about the system working

Check out the system fully working in this video: https://www.youtube.com/watch?v=O-oL2GZGN28
Unfortunately we forgot to record localization, but if you want to check that I am not lying, I have another repo about localization, same algorithm, and you can run it by hand. :D

### If you have any thoughts or questions feel free to hit me up.
