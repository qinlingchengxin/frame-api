package net.ys.service;

import net.ys.bean.Person;
import net.ys.dao.PersonDao;
import net.ys.utils.LogUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class PersonService {
    @Resource
    private PersonDao personDao;

    public long queryPersonCount(String name) {
        return personDao.queryPersonCount(name);
    }

    public List<Person> queryPersons(String name, int page, int pageSize) {
        return personDao.queryPersons(name, page, pageSize);
    }

    public Person queryPerson(String id) {
        return personDao.queryPerson(id);
    }

    public boolean savePerson(Person person) {
        return personDao.savePerson(person);
    }

    public Person addPerson(Person person) {
        return personDao.addPerson(person);
    }

    public List<Person> readExcelPersons(MultipartFile file) throws IOException {
        List<Person> persons = new ArrayList<Person>();
        InputStream is = file.getInputStream();
        Workbook wb = new HSSFWorkbook(is);
        Sheet sheet = wb.getSheetAt(0);
        Person person;
        int lastRowNum = sheet.getLastRowNum() + 1;//默认第一行为标题
        if (lastRowNum < 2) {
            return null;
        }

        short lastCellNum = sheet.getRow(1).getLastCellNum();

        if (lastCellNum == -1) {//第二行如没有数据，则直接返回，可根据实际情况修改
            return null;
        }

        Row row;
        Cell cell;
        String val;

        for (int k = 1; k < lastRowNum; k++) {
            person = new Person();

            row = sheet.getRow(k);
            lastCellNum = row.getLastCellNum();
            if (lastCellNum == -1) {
                continue;
            }
            for (int i = 0, j = row.getLastCellNum(); i < j; i++) {
                cell = row.getCell(i);
                if (cell == null) {
                    val = "";
                } else {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    val = cell.getStringCellValue().trim();
                }

                switch (i) {
                    case 0:
                        person.setId(Integer.parseInt("".equals(val) ? "0" : val));
                        break;
                    case 1:
                        person.setName(val);
                        break;
                    case 2:
                        person.setAge(Integer.parseInt("".equals(val) ? "0" : val));
                        break;
                    case 3:
                        person.setCreateTime(Long.parseLong("".equals(val) ? "0" : val));
                        break;
                    default:
                        LogUtil.info("excel format error!!!");
                }
            }
            if (!person.checkEmpty()) {
                persons.add(person);
            }
        }
        return persons;
    }
}
