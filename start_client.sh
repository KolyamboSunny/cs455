CLASSES="/s/bach/k/under/nsunny/Documents/cs455/cs455/build/classes/java/main/"
SCRIPT="cd $CLASSES; java -cp . cs455.scaling.client.Client denver 6868 10 &"
for ((j=1;j<=$1;j++));
do
	eval $SCRIPT
done
