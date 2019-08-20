# List of source files required by libusf and any emulators

set(libusf_SRCS)

include_directories(${CMAKE_CURRENT_SOURCE_DIR})

# Add library to be compiled.
add_library(usf SHARED ${libusf_SRCS}
        android/Backend.cpp
        audio.c
        cpu.c
        dma.c
        exception.c
        interpreter_cpu.c
        interpreter_ops.c
        main.c
        pif.c
        registers.c
        tlb.c
        usf.c
        memory.c
        cpu_hle.c
        audiolib.c
        os.c
        rsp/rsp.c
        rsp_hle/alist.c
        rsp_hle/alist_nead.c
        rsp_hle/jpeg.c
        rsp_hle/mp3.c
        rsp_hle/alist_audio.c
        rsp_hle/audio.c
        rsp_hle/hle.c
        rsp_hle/musyx.c
        rsp_hle/alist_naudio.c
        rsp_hle/cicx105.c
        rsp_hle/memory.c
        rsp_hle/plugin.c
        )

# Not sure what most of this means, but apparently it links to "android.h", "zlib.h" and "log.h"
if (ANDROID)
    target_link_libraries(usf
            log
            android
            z
            psflib
            samplerate
            "-Wl,--no-warn-mismatch"
            "-Wl,--whole-archive"
            ${LIBS}
            "-Wl,--no-whole-archive"
            )
endif ()