# 🚀 BUILD & DEPLOYMENT GUIDE - Kronus Rift Engine

## Quick Start

```bash
cd c_game
mkdir build && cd build
cmake ..
cmake --build . --config Release -j4
./KronusRiftGame
```

---

## Instalação Detalhada

### Windows (MSVC)

```bash
# 1. Instalar Visual Studio 2019+ com C++ tools
# 2. Instalar CMake 3.20+
# 3. Clonar repo

cd c_game
mkdir build
cd build

# 4. Generate
cmake .. -G "Visual Studio 17 2022" -A x64

# 5. Build
cmake --build . --config Release --parallel 4

# 6. Run
.\Release\KronusRiftGame.exe
```

### Linux (GCC/Clang)

```bash
# Install dependencies
sudo apt-get install build-essential cmake git libx11-dev libxrandr-dev \
    libxinerama-dev libxext-dev libxcursor-dev libasound2-dev

cd c_game
mkdir build && cd build
cmake ..
make -j$(nproc)
./KronusRiftGame
```

### macOS (Clang)

```bash
# Install dependencies
brew install cmake

cd c_game
mkdir build && cd build
cmake ..
make -j$(sysctl -n hw.ncpu)
./KronusRiftGame
```

---

## Requisitos de Build

| Componente | Versão | Papel |
|-----------|--------|-------|
| **CMake** | 3.20+ | Build system |
| **C++ Compiler** | C++17 | MSVC 2019+, GCC 9+, Clang 11+ |
| **Raylib** | 4.5.0 | Renderer (auto-download via FetchContent) |
| **glm** | Latest | Math library (header-only) |
| **nlohmann/json** | 3.11.2 | JSON parsing (auto-download) |

---

## Estrutura de Build

```
c_game/
├── CMakeLists.txt           # Configuração CMake
├── include/                 # Headers (.hpp)
├── src/                     # Implementações (.cpp)
├── build/                   # Saída de build (gitignore)
│   ├── CMakeFiles/
│   ├── KronusRiftGame[.exe]
│   └── ...
└── res/                     # Assets (sprites, maps)
```

---

## Troubleshooting

### ❌ "raylib not found"
**Solução**: FetchContent vai baixar automaticamente. Se falhar:
```bash
cd build
cmake .. -DCMAKE_PREFIX_PATH=~/.local
```

### ❌ "undefined reference to `main`"
**Solução**: Certifique-se que `src/main.cpp` existe e está listado em CMakeLists.txt

### ❌ Linking fails on Linux
**Solução**: Instale libx11:
```bash
sudo apt-get install libx11-dev libxrandr-dev
```

### ❌ Slow compilation
**Solução**: Use compilação paralela:
```bash
cmake --build . -j4  # 4 threads
```

---

## Release Build

### Otimização para Release

```bash
cmake .. -DCMAKE_BUILD_TYPE=Release
cmake --build . --config Release --parallel 4

# Binário otimizado: ./build/KronusRiftGame
```

### Criando Distributable

```bash
# Windows
cd build/Release
tar -czf KronusRiftGame-win64.tar.gz KronusRiftGame.exe

# Linux
cd build
strip KronusRiftGame
tar -czf KronusRiftGame-linux64.tar.gz KronusRiftGame

# macOS
cd build
strip KronusRiftGame
tar -czf KronusRiftGame-macos.tar.gz KronusRiftGame
```

---

## CI/CD Setup (GitHub Actions)

### `.github/workflows/build.yml`

```yaml
name: Build Kronus Rift

on: [push, pull_request]

jobs:
  build:
    strategy:
      matrix:
        os: [ubuntu-latest, windows-latest, macos-latest]
    
    runs-on: ${{ matrix.os }}
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Install dependencies (Ubuntu)
        if: runner.os == 'Linux'
        run: |
          sudo apt-get update
          sudo apt-get install -y libx11-dev libxrandr-dev libxinerama-dev
      
      - name: Create build directory
        run: mkdir -p c_game/build
      
      - name: Configure CMake
        working-directory: c_game/build
        run: cmake ..
      
      - name: Build
        working-directory: c_game/build
        run: cmake --build . --config Release -j4
      
      - name: Upload artifacts
        uses: actions/upload-artifact@v3
        with:
          name: KronusRiftGame-${{ matrix.os }}
          path: c_game/build/KronusRiftGame*
```

---

## Debug Build

Para desenvolver com debug symbols:

```bash
cmake .. -DCMAKE_BUILD_TYPE=Debug
cmake --build . --config Debug -j4

# Executar com GDB (Linux/macOS)
gdb ./KronusRiftGame

# Executar com debugger (Windows)
msvcrt.exe .\Debug\KronusRiftGame.exe
```

---

## Verificação de Build

Após compilação bem-sucedida:

```bash
# Listar arquivo executável
ls -lh build/KronusRiftGame

# Verificar dependências dinâmicas (Linux)
ldd build/KronusRiftGame

# Testar execução
./build/KronusRiftGame --help  # Se implementado

# Executar
./build/KronusRiftGame
```

---

## Assets & Resources

```
c_game/res/
├── sprites/
│   ├── player/
│   │   ├── warrior.png
│   │   ├── hunter.png
│   │   └── mage.png
│   └── enemies/
│       ├── goblin.png
│       ├── wolf.png
│       └── ...
├── maps/
│   ├── eldoria-forest.png
│   └── tile-set.png
└── fonts/
    └── arial.ttf
```

> ⚠️ Assets não incluídos. Use `res/map-eldora.png` do projeto Java!

---

## Performance Optimization

### CMakeLists.txt customization

```cmake
# Enable optimizations
if (MSVC)
    target_compile_options(KronusRiftGame PRIVATE /O2 /fp:fast)
else()
    target_compile_options(KronusRiftGame PRIVATE -O3 -march=native)
endif()

# Link Time Optimization
set(CMAKE_INTERPROCEDURAL_OPTIMIZATION TRUE)
```

### Runtime tweaks (Game.hpp)

```cpp
// Reduzir resolução para melhor FPS
const int screenWidth = 1280;   // De 1600
const int screenHeight = 720;   // De 900

// Reduzir grid rendering
static constexpr int TILE_SIZE = 64;  // De 32
```

---

## Deployment

### Standalone Executable

```bash
# Criar pasta com tudo necessário
mkdir -p Kronus-Rift-Bin
cd Kronus-Rift-Bin

# Copiar executable
cp ../build/KronusRiftGame .

# Copiar assets (opcional)
mkdir -p res/
cp ../c_game/res/* res/

# Distribuir
zip -r Kronus-Rift-v1.0.zip *
```

---

## Próximos Passos

1. ✅ **Build bem-sucedido** → Executar `KronusRiftGame`
2. **Integração Java** → Ver `GUIA_INTEGRACAO_JAVA_CPP.md`
3. **Assets** → Adicionar sprites em `res/`
4. **Multiplayer** → Implementar networking via WebSocket
5. **Releases** → GitHub Releases com binários

---

**Pronto para jogar! 🎮**
