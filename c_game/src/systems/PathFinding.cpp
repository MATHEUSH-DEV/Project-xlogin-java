#include "systems/PathFinding.hpp"
#include <glm/glm.hpp>
#include <algorithm>
#include <queue>

std::vector<glm::vec2> PathFinding::findPath(
    glm::vec2 start,
    glm::vec2 goal,
    const std::vector<glm::vec4>& obstacles,
    float gridSize) {
    
    // Simple straight-line path for now
    // TODO: Implement full A* pathfinding
    
    std::vector<glm::vec2> path;
    glm::vec2 current = start;
    
    while (glm::distance(current, goal) > gridSize) {
        glm::vec2 direction = glm::normalize(goal - current);
        current += direction * gridSize;
        path.push_back(current);
    }
    
    path.push_back(goal);
    return path;
}

float PathFinding::heuristic(glm::vec2 a, glm::vec2 b) {
    return glm::distance(a, b);
}

bool PathFinding::isWalkable(glm::vec2 pos, const std::vector<glm::vec4>& obstacles) {
    // TODO: Check collision with obstacles
    return true;
}
