#include <string>

bool assertStringEqual(char * c1,char * c2){
    std::string cs1 = c1;
    std::string cs2 = c2;
    return cs1 == cs2;
}
