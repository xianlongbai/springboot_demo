package com.bxl.entity;

public class TestGoods {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_goods.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_goods.name
     *
     * @mbg.generated
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column test_goods.price
     *
     * @mbg.generated
     */
    private String price;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test_goods
     *
     * @mbg.generated
     */
    public TestGoods(Integer id, String name, String price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test_goods
     *
     * @mbg.generated
     */
    public TestGoods() {
        super();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_goods.id
     *
     * @return the value of test_goods.id
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_goods.id
     *
     * @param id the value for test_goods.id
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_goods.name
     *
     * @return the value of test_goods.name
     *
     * @mbg.generated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_goods.name
     *
     * @param name the value for test_goods.name
     *
     * @mbg.generated
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column test_goods.price
     *
     * @return the value of test_goods.price
     *
     * @mbg.generated
     */
    public String getPrice() {
        return price;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column test_goods.price
     *
     * @param price the value for test_goods.price
     *
     * @mbg.generated
     */
    public void setPrice(String price) {
        this.price = price == null ? null : price.trim();
    }
}