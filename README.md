# Galaxy Momentum
Deployed at https://vbelles.github.io/galaxy-momentum/

Game in development for [libGDX Jam 18](https://itch.io/jam/libgdx-jam-18)

# Instructions

1. Install java (openjdk 11 and 13 worked but 16+ did not)
1. Run `gradlew.bat html:dist` or `./gradlew html:dist` depending on your operating system
1. Start a simple static HTTP server in the `html/build/dist` directory
    - Example: `python3 -m http.server 8000 --bind 127.0.0.1`
1. Visit http://localhost:8000 to play and press `[D]` for debugging
