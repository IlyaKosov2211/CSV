package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, TransformerException, IOException, SAXException {

        String[] employee = "1,John,Smith,USA,25".split(",");
        String[] employee2 = "2,Ivan,Petrov,RU,23".split(",");
        try (CSVWriter writer = new CSVWriter(new FileWriter("data.csv"))) {
            writer.writeNext(employee);
            writer.writeNext(employee2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element staff = document.createElement("staff");
        document.appendChild(staff);
        Element employee_1 = document.createElement("employee");
        staff.appendChild(employee_1);
        Element id = document.createElement("id");
        id.appendChild(document.createTextNode("1"));
        employee_1.appendChild(id);
        Element firstName = document.createElement("firstName");
        firstName.appendChild(document.createTextNode("John"));
        employee_1.appendChild(firstName);
        Element lastName = document.createElement("lastName");
        lastName.appendChild(document.createTextNode("Smith"));
        employee_1.appendChild(lastName);
        Element country = document.createElement("country");
        country.appendChild(document.createTextNode("USA"));
        employee_1.appendChild(country);
        Element age = document.createElement("age");
        age.appendChild(document.createTextNode("25"));
        employee_1.appendChild(age);

        Element employee_2 = document.createElement("employee");
        staff.appendChild(employee_2);
        Element id_2 = document.createElement("id");
        id_2.appendChild(document.createTextNode("2"));
        employee_2.appendChild(id);
        Element firstName_2 = document.createElement("firstName");
        firstName_2.appendChild(document.createTextNode("Ivan"));
        employee_2.appendChild(firstName);
        Element lastName_2 = document.createElement("lastName");
        lastName_2.appendChild(document.createTextNode("Petrov"));
        employee_2.appendChild(lastName);
        Element country_2 = document.createElement("country");
        country_2.appendChild(document.createTextNode("RU"));
        employee_2.appendChild(country);
        Element age_2 = document.createElement("age");
        age_2.appendChild(document.createTextNode("23"));
        employee_2.appendChild(age);

        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File("data.xml"));
        TransformerFactory transformerFactory= TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, streamResult);

        List<Employee> list2 = parseXML("data.xml");
        System.out.println(list2);
        String json2 = listToJson(list2);
        writeString(json2, "data2.json");

    }


    public static List parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> staff = csv.parse();
            return staff;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return List.of();
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String s, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(s);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Employee> parseXML(String s) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> list = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(s));

            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("employee");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Employee employee = new Employee(Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent()),
                            element.getElementsByTagName("firstName").item(0).getTextContent(),
                            element.getElementsByTagName("lastName").item(0).getTextContent(),
                            element.getElementsByTagName("country").item(0).getTextContent(),
                            Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent()));
                    list.add(employee);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}