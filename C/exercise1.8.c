#include <stdio.h>

main(){
	int c;
	int btn=0;
	//btn=0;
	while((c=getchar())!=EOF){
		if((c==' ')||(c=='\t')||(c=='\n')){
			++btn;
		}
	}
	printf("%d\n",btn);
}