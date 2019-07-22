#指定支持的最低版本
cmake_minimum_required(VERSION 3.4.1)


#引入指定目录下的CMakeList.txt
add_subdirectory(src/main/cpp/librtmp)

#include_directories(ffmpeg/include/ src/main/res player)



add_library(plugflow SHARED
        src/main/cpp/safe_queue.h
        src/main/cpp/native-lib.cpp
        src/main/cpp/VideoChannel.cpp
        src/main/cpp/VideoChannel.h
#        src/main/cpp/librtmp/amf.c
#        src/main/cpp/librtmp/hashswf.c
#        src/main/cpp/librtmp/log.c
#        src/main/cpp/librtmp/rtmp.c
        )

include_directories(src/main/cpp/include)

#指定查找静态库的路径
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/src/main/cpp/libs/${ANDROID_ABI}")

find_library(log log)
# 链接其他库，从上面设置的目录中找
target_link_libraries(plugflow
        log
        rtmp
        x264
        #        z
#        android
        #        OpenSLES
        )