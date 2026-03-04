cmake_minimum_required(VERSION 3.20)
project(KronusRiftGame VERSION 1.0.0 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# ===== RAYLIB =====
set(RAYLIB_VERSION 4.5.0)
find_package(raylib ${RAYLIB_VERSION} QUIET)

if (NOT raylib_FOUND)
    include(FetchContent)
    FetchContent_Declare(raylib
        URL https://github.com/raysan5/raylib/archive/refs/tags/${RAYLIB_VERSION}.tar.gz
    )
    FetchContent_GetProperties(raylib)
    if (NOT raylib_POPULATED)
        set(FETCHCONTENT_QUIET NO)
        FetchContent_Populate(raylib)
        set(BUILD_SHARED_LIBS OFF CACHE BOOL "" FORCE)
        set(BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
        add_subdirectory(${raylib_SOURCE_DIR} ${raylib_BINARY_DIR})
    endif()
endif()

# ===== JSON PARSING (nlohmann/json) =====
include(FetchContent)
FetchContent_Declare(json URL https://github.com/nlohmann/json/releases/download/v3.11.2/include.zip)
FetchContent_MakeAvailable(json)

# ===== MAIN EXECUTABLE =====
add_executable(KronusRiftGame
    src/main.cpp
    src/game/Game.cpp
    src/game/GameWorld.cpp
    src/game/GameState.cpp
    src/entities/Player.cpp
    src/entities/Enemy.cpp
    src/entities/Ability.cpp
    src/systems/InputHandler.cpp
    src/systems/CombatSystem.cpp
    src/systems/PathFinding.cpp
    src/systems/NetworkClient.cpp
    src/ui/UIManager.cpp
    src/ui/HUD.cpp
    src/util/AssetManager.cpp
    src/util/Logger.cpp
)

target_include_directories(KronusRiftGame PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/include
    ${nlohmann_json_SOURCE_DIR}/include
)

target_link_libraries(KronusRiftGame PRIVATE
    raylib
    nlohmann_json::nlohmann_json
)

# ===== PLATFORM-SPECIFIC =====
if (WIN32)
    target_link_libraries(KronusRiftGame PRIVATE ws2_32)
elseif(UNIX AND NOT APPLE)
    target_link_libraries(KronusRiftGame PRIVATE pthread)
endif()

# ===== COMPILER FLAGS =====
if(MSVC)
    target_compile_options(KronusRiftGame PRIVATE /W4 /WX)
else()
    target_compile_options(KronusRiftGame PRIVATE -Wall -Wextra -Wpedantic)
endif()
