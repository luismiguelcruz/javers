package org.javers.core.cases;

/**
 * @author Luis Miguel Cruz
 */
public class EmbeddedArrays {
  @Test
  void test() {
    Javers javers = JaversBuilder.javers().build();
    final List<TypeOfPayment> typesOfPayment = List.of(new TypeOfPayment("payment1"));
    final Claims claims = new Claims(List.of("item1", "item2"), typesOfPayment);
    final SubClass section = new SubClass("section1", claims);
    MainClass lineOfBusinessSettings1 = new MainClass("id1", section);

    // Updating TypesOfPayment for Claims
    final List<TypeOfPayment> typesOfPayment2 = List.of(new TypeOfPayment("payment2"));
    final Claims claims2 = new Claims(List.of("item1", "item2"), typesOfPayment2);
    final SubClass section2 = new SubClass("section1", claims2);
    MainClass lineOfBusinessSettings2 = new MainClass("id1", section2);

    // Updating TypesOfLoss for Claims
    final Claims claims3 = new Claims(List.of("item1", "item2", "item3"), typesOfPayment2);
    final SubClass section3 = new SubClass("section1", claims3);
    MainClass lineOfBusinessSettings3 = new MainClass("id1", section3);

    javers.commit("test", lineOfBusinessSettings1);
    javers.commit("test", lineOfBusinessSettings2);
    javers.commit("test", lineOfBusinessSettings3);

    // Retrieving the changes on Claims
    List<Shadow<Claims>> id1 =
      javers.findShadows(QueryBuilder.byValueObjectId("id1", MainClass.class, "mainSection/claims").withScopeCommitDeep()
        .build());

    assertThat(id1).hasSize(3);

    // Retrieving the changes on TypesOfLoss
    List<Shadow<Object>> id2 =
      javers.findShadows(QueryBuilder.byValueObjectId("id1", MainClass.class, "mainSection/claims/typesOfLoss").withScopeCommitDeep()
        .build());

    assertThat(id2).hasSize(2);

    // Retrieving the changes on TypesOfPayment
    List<Shadow<Object>> id3 =
      javers.findShadows(QueryBuilder.byValueObjectId("id1", MainClass.class, "mainSection/claims/typesOfPayment").withScopeCommitDeep()
        .build());

    assertThat(id3).hasSize(2);

    System.out.println();
  }
}

class MainClass {
  @Id
  private String id;
  private SubClass mainSection;

  public MainClass(String id, SubClass mainSection) {
    this.id = id;
    this.mainSection = mainSection;
  }
}

class SubClass {
  private String name;
  private Claims claims;

  public SubClass(String name, Claims claims) {
    this.name = name;
    this.claims = claims;
  }
}

class Claims {
  private List<String> typesOfLoss;
  private List<TypeOfPayment> typesOfPayment;

  public Claims(List<String> typesOfLoss, List<TypeOfPayment> typesOfPayment) {
    this.typesOfLoss = typesOfLoss;
    this.typesOfPayment = typesOfPayment;
  }
}

class TypeOfPayment {
  private String name;

  public TypeOfPayment(String name) {
    this.name = name;
  }
}
