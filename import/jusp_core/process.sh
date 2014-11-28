for line in `cat coretitle-all.csv`
do
  inst=`echo $line | cut -d, -f1`
  title=`echo $line | cut -d, -f2`
  year=`echo $line | cut -d, -f3`

  echo $inst $title $year
  curl "http://localhost:8080/demo/api/assertCore?inst=jusplogin:$inst&title=jusp:$title&year=$year"
done
