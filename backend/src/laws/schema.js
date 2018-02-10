import { makeExecutableSchema } from 'graphql-tools';

import resolvers from './resolvers';

const typeDefs = `
    type Law {
        id: String
        title: String
        abstract: String
        resource: String
        affectations: [Affectation]
    }

    type Query {
        laws: [Law]
        law(id: Int!): Law
    }
    
    type Mutation {
        addLaw(title: String!, abstract: String, resource: String!, affectations: [Affectation]): Law
    }
    
    enum Affectation {
        PROFESSIONAL
        RECREATIONAL
        DRAGGER
    }
`;

const schema = makeExecutableSchema({typeDefs, resolvers});
export default schema;
