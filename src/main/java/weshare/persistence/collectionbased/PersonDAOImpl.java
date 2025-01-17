package weshare.persistence.collectionbased;

/*
 ** DO NOT CHANGE!!
 */


import weshare.model.Person;
import weshare.persistence.PersonDAO;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PersonDAOImpl implements PersonDAO {
    private final Set<Person> setOfPeople;

    public PersonDAOImpl() {
        setOfPeople = new HashSet<>();
    }

    public PersonDAOImpl(Collection<Person> people) {
        setOfPeople = new HashSet<>(people);
    }

    @Override
    public Optional<Person> findPersonByEmail(String email) {
        return setOfPeople.stream().filter(p -> p.getEmail().equals(email)).findFirst();
    }

    @Override
    public Person savePerson(Person person) {
        if (findPersonByEmail(person.getEmail()).isEmpty()) setOfPeople.add(person);
        return person;
    }
}
