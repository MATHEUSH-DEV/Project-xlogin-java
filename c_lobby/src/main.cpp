#include <iostream>
#include <string>
#include <thread>
#include <chrono>

int main(int argc, char** argv) {
    std::string userId = (argc > 1) ? argv[1] : "unknown";
    std::cout << "Kronus C++ Lobby (beta)" << std::endl;
    std::cout << "User connected: " << userId << std::endl;
    std::cout << "Type 'exit' then ENTER to quit the lobby." << std::endl;

    // Simple loop that waits for user input. Minimal UI for beta lobby.
    std::string line;
    while (true) {
        std::cout << "> ";
        if (!std::getline(std::cin, line)) break;
        if (line == "exit") {
            std::cout << "Shutting down lobby for user " << userId << std::endl;
            break;
        }
        // simulate a tiny lobby response
        if (line == "ping") {
            std::cout << "pong" << std::endl;
            continue;
        }
        std::cout << "(lobby) unknown command: '" << line << "' - try 'ping' or 'exit'" << std::endl;
    }

    // graceful shutdown delay
    std::this_thread::sleep_for(std::chrono::milliseconds(200));
    return 0;
}
