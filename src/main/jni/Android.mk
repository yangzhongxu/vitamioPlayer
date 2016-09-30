LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := gogo

LOCAL_SRC_FILES := gogo.cpp util.cpp

LOCAL_LDLIBS    := -llog -ljnigraphics

#c++异常支持  没用到..凑个热闹
LOCAL_CPPFLAGS += -fexceptions

include $(BUILD_SHARED_LIBRARY)


#包含全部子目录的mk文件 ..  没用
#include $(call all-makefiles-under,$(LOCAL_PATH))

