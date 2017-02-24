import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class PeopleWeb {

    static ArrayList<Person> people = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException {

        people = scanFileIntoArray("people.csv");

        Spark.init();

        Spark.get("/", (
                        (request, response) -> {
                            String temp = request.queryParams("num");
                            int offset = 0;

                            if (temp!=null) {
                                offset = Integer.parseInt(temp);
                            }
                            List<Person> tempo = people.subList(offset, offset+20);

                            HashMap m = new HashMap();
                            m.put("offset", offset -20);
                            m.put("offset++", offset +20);
                            m.put("people", tempo);
                            return new ModelAndView(m, "people.html");
                        }),
                new MustacheTemplateEngine()
        );

        Spark.get("/person", (
                        (request, response) -> {
                            String id = request.queryParams("id");
                            Person person = people.get(Integer.parseInt(id) -1);

                            HashMap m = new HashMap();
                            m.put("person", person);
                            return new ModelAndView(m, "person.html");
                        }),
                new MustacheTemplateEngine()
        );
    }

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

}
