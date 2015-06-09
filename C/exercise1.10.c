#include <stdio.h>

main(){
	int c;
	while((c=getchar())!=EOF){
		if(c=='\t'||c=='\b'||c=='/'){
			putchar('\\');
			putchar('\\');	
		} else {
			putchar(c);
		}
	}
}
