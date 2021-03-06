import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class PeopleWeb {

    static ArrayList<Person> scanFileIntoArray(String filename) throws FileNotFoundException {
        File f = new File(filename);
        Scanner fileScanner = new Scanner(f);
        ArrayList<Person> people = new ArrayList<>();
        while (fileScanner.hasNext()) {
            String line = fileScanner.nextLine();
            String[] columns = line.split("\\,");
            Person person = new Person(columns[0], columns[1], columns[2], columns[3], columns[4], columns[5]);
            people.add(person);
        }
        fileScanner.close();
        return people;
    }


    public static void main(String[] args) throws FileNotFoundException {

        ArrayList<Person> people = scanFileIntoArray("people.csv");
        int peopleSize = people.size();

        Spark.init();

        Spark.get("/", (
                        (request, response) -> {
                            int peopleIndex = 0;
                            String stringIndex = request.queryParams("i");
                            if (stringIndex != null) {
                                peopleIndex = Integer.parseInt(stringIndex);
                            }

                            int pageSize = 20;

                            List<Person> pageOfPeople = people.subList(peopleIndex, peopleIndex + pageSize);
                            HashMap m = new HashMap();

                            if (peopleIndex >= pageSize) {
                                m.put("backIndex", peopleIndex - pageSize);
                            }
                            if (peopleIndex <= (peopleSize - (2 * pageSize))) {
                                m.put("forwardIndex", peopleIndex + pageSize);
                            }
                            m.put("pageSize", pageSize);
                            m.put("people", pageOfPeople);
                            return new ModelAndView(m, "people.html");
                        }),
                new MustacheTemplateEngine()
        );

        Spark.get("/person", (
                        (request, response) -> {
                            String id = request.queryParams("id");
                            Person person = people.get(Integer.parseInt(id) - 1);

                            HashMap m = new HashMap();
                            m.put("person", person);
                            return new ModelAndView(m, "person.html");
                        }),
                new MustacheTemplateEngine()
        );
    }


}
