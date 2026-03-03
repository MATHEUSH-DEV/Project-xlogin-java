cmake_minimum_required(VERSION 3.10)
project(kronus_c_lobby VERSION 0.1 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)

# Coloca o executável gerado diretamente na pasta c_lobby/ para que o
# launcher Java encontre `c_lobby/lobby` (ou `lobby.exe` no Windows).
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_SOURCE_DIR})

add_executable(lobby src/main.cpp)
