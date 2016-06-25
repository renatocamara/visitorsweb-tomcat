package com.oracle.tomcat.example.visitorsweb;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Visitor implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;    
    private String name;	
    private String allComments;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateVisited;	
    	
    public Long getId() {
        return this.id;
    }	
    
    public void setId(final Long id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setAllComments(final String allComments) {
        this.allComments = allComments;
    }
    
    public String getAllComments() {
        return this.allComments;
    }
    
    public void setDateVisited(final Date dateVisited) {
        this.dateVisited = dateVisited;
    }
    
    public Date getDateVisited() {
        return this.dateVisited;
    }
    
    public Visitor() {
        super();
    }
    
    public Visitor(final String name, final String allComments, final Date dateVisited) {
        super();
        this.name = name;
        this.dateVisited = dateVisited;
        this.allComments = allComments;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += ((this.id != null) ? this.id.hashCode() : 0);
        hash += ((this.name != null) ? this.name.hashCode() : 0);
        hash += ((this.allComments != null) ? this.allComments.hashCode() : 0);
        hash += ((this.dateVisited != null) ? this.dateVisited.hashCode() : 0);
        return hash;
    }
    
    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof Visitor)) {
            return false;
        }
        final Visitor other = (Visitor)object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id)) && (this.name != null || other.name == null) && (this.name == null || this.name.equals(other.name)) && (this.allComments != null || other.allComments == null) && (this.allComments == null || this.allComments.equals(other.allComments)) && (this.dateVisited != null || other.dateVisited == null) && (this.dateVisited == null || this.dateVisited.equals(other.dateVisited));
    }
    
    @Override
    public String toString() {
        return "com.oracle.tomcat.example.visitorsweb.Visitor[ id=" + this.id + "name=" + this.name + "allComments=" + this.allComments + "dateVisited=" + this.dateVisited + " ]";
    }
    
    
}
