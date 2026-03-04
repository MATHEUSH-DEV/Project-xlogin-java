#pragma once

#include <glm/glm.hpp>
#include <vector>
#include <queue>

/**
 * A* Pathfinding para movimento by-click (estilo LoL/Dota)
 */
class PathFinding {
public:
    struct Node {
        glm::vec2 position;
        float gCost = 0.0f;
        float hCost = 0.0f;
        float fCost = 0.0f;
        Node* parent = nullptr;

        float getCost() const { return gCost + hCost; }
    };

    static std::vector<glm::vec2> findPath(
        glm::vec2 start,
        glm::vec2 goal,
        const std::vector<glm::vec4>& obstacles,
        float gridSize = 20.0f
    );

private:
    static float heuristic(glm::vec2 a, glm::vec2 b);
    static bool isWalkable(glm::vec2 pos, const std::vector<glm::vec4>& obstacles);
};
