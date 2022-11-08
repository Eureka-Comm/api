package com.castellanos94.fuzzylogic.api.utils;

import com.castellanos94.fuzzylogicgp.core.*;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunctionFactory;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunctionType;
import com.castellanos94.fuzzylogicgp.parser.MembershipFunctionSerializer;
import com.castellanos94.jfuzzylogic.core.OperatorUtil;
import com.castellanos94.jfuzzylogic.core.base.AElement;
import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.OperatorType;
import com.castellanos94.jfuzzylogic.core.base.impl.*;
import com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction;
import com.castellanos94.jfuzzylogic.core.membershipfunction.impl.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class TransformPredicate {
    private static final Logger logger = LoggerFactory.getLogger(TransformPredicate.class);

    private static final Gson gson = new GsonBuilder().
            registerTypeAdapter(com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction.class, new MembershipFunctionSerializer())
            .registerTypeAdapter(com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction.class, new MembershipFunctionTransform())
            .create();

    public static Operator transform(NodeTree input) throws JsonProcessingException {
        logger.debug("Input {}", input);
        if (input == null)
            return null;
        if (input.getChildren().size() == 1 && input.getChildren().get(0) instanceof GeneratorNode) {
            return transformGenerator((GeneratorNode) input.getChildren().get(0));
        }
        Operator predicate = OperatorUtil.getInstance(OperatorType.valueOf(input.getType().name()));
        if (input.getType() == NodeType.IMP) {
            Imp imp = (Imp) predicate;
            Node nd = null;
            for (Node _n : input) {
                if (_n.getId().equalsIgnoreCase(input.getLeftID())) {
                    nd = _n;
                    break;
                }
            }
            imp.setAntecedent(transformSelect(nd));
            for (Node _n : input) {
                if (_n.getId().equalsIgnoreCase(input.getRighID())) {
                    nd = _n;
                    break;
                }
            }
            imp.setConsequent(transformSelect(nd));
        } else {
            for (Node node : input) {
                predicate.add(transformSelect(node));
            }
        }
        return predicate;
    }

    private static AElement transformSelect(Node node) throws JsonProcessingException {
        if (node instanceof StateNode) {
            return transformToState((StateNode) node);
        } else if (node instanceof GeneratorNode) {
            return transformGenerator((GeneratorNode) node);
        } else {
            return transform((NodeTree) node);
        }
    }

    private static Generator transformGenerator(GeneratorNode generatorNode) {
        Generator generator = new Generator();
        generator.setLabel(generatorNode.getLabel());
        generator.setDepth(generatorNode.getDepth());
        generator.setFrom(generatorNode.getByGenerator());
        generator.setEditable(generatorNode.isEditable());
        generator.setDescription(generatorNode.getDescription());
        generator.setMaxChild(generatorNode.getMax_child_number());
        for (NodeType _nt : generatorNode.getOperators()) {
            generator.add(OperatorType.valueOf(_nt.name()));
        }
        generatorNode.getVariables().forEach(node -> {
            if (node instanceof StateNode) {
                try {
                    generator.add(transformToState((StateNode) node));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            } else if (node instanceof GeneratorNode) {
                generator.add(transformGenerator((GeneratorNode) node));
            }
        });
        return generator;

    }

    private static State transformToState(StateNode node) throws JsonProcessingException {
        State state = new State();
        state.setDescription(node.getDescription());
        state.setFrom(node.getByGenerator());
        state.setLabel(node.getLabel());
        state.setEditable(node.isEditable());
        state.setColName(node.getColName());
        if (node.getMembershipFunction() != null) {
            String st = gson.toJson(node.getMembershipFunction());
            state.setMembershipFunction(gson.fromJson(st, com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction.class));
        }
        return state;
    }

    public static NodeTree convertToOldVersion(Operator predicate) throws OperatorException {
        NodeTree tree = new NodeTree(getNodeTypeRef(predicate));
        tree.setLabel(predicate.getLabel());
        tree.setDescription(predicate.getDescription());
        tree.setFitness(predicate.getFitness());
        tree.setEditable(predicate.isEditable());
        tree.setByGenerator(predicate.getFrom());

        if (predicate instanceof Imp) {
            tree.addChild(convertToOldVersionDecision(((Imp) predicate).getAntecedent()));
            tree.addChild(convertToOldVersionDecision(((Imp) predicate).getConsequent()));
        } else {
            for (AElement element : predicate) {
                tree.addChild(convertToOldVersionDecision(element));
            }
        }
        return tree;
    }

    private static Node convertToOldVersionDecision(AElement element) throws OperatorException {
        if (element == null)
            return null;
        if (element instanceof State) {
            StateNode st = new StateNode();
            st.setColName(((State) element).getColName());
            st.setLabel(element.getLabel());
            if (((State) element).getMembershipFunction() instanceof MapNominal) {
                MapNominal f = (MapNominal) ((State) element).getMembershipFunction();
                com.castellanos94.fuzzylogicgp.membershipfunction.MapNominal mn = new com.castellanos94.fuzzylogicgp.membershipfunction.MapNominal();
                mn.setNotFoundValue(f.getNotFoundValue());
                mn.setValues(f.getValues());
                st.setMembershipFunction(mn);
            } else {
                st.setMembershipFunction(MembershipFunctionFactory.fromArray(getMembershipFunctionTypeByRef(((State) element).getMembershipFunction()), ((State) element).getMembershipFunction().toArray()));
            }
            st.setDescription(element.getDescription());
            st.setEditable(element.isEditable());
            st.setByGenerator(element.getFrom());
            return st;
        } else {
            return convertToOldVersion((Operator) element);
        }
    }

    private static MembershipFunctionType getMembershipFunctionTypeByRef(MembershipFunction f) {
        if (f == null) {
            return null;
        }
        if (f instanceof FPG) {
            return MembershipFunctionType.FPG;
        }
        if (f instanceof Sigmoid) {
            return MembershipFunctionType.SIGMOID;
        }
        if (f instanceof NSigmoid) {
            return MembershipFunctionType.NSIGMOID;
        }
        if (f instanceof Gamma) {
            return MembershipFunctionType.GAMMA;
        }
        if (f instanceof Gaussian) {
            return MembershipFunctionType.GAUSSIAN;
        }
        if (f instanceof GBell) {
            return MembershipFunctionType.GBELL;
        }
        if (f instanceof LGamma) {
            return MembershipFunctionType.LGAMMA;
        }
        if (f instanceof LTrapezoidal) {
            return MembershipFunctionType.LTRAPEZOIDAL;
        }
        if (f instanceof RTrapezoidal) {
            return MembershipFunctionType.RTRAPEZOIDAL;
        }
        if (f instanceof SForm) {
            return MembershipFunctionType.SFORM;
        }
        if (f instanceof PseudoExp) {
            return MembershipFunctionType.PSEUDOEXP;
        }
        if (f instanceof Singleton) {
            return MembershipFunctionType.SINGLETON;
        }
        if (f instanceof Triangular) {
            return MembershipFunctionType.TRIANGULAR;
        }
        if (f instanceof Trapezoidal) {
            return MembershipFunctionType.TRAPEZOIDAL;
        }
        if (f instanceof ZForm) {
            return MembershipFunctionType.ZFORM;
        }
        if (f instanceof MapNominal) {
            return MembershipFunctionType.MAPNOMIAL;
        }
        return null;
    }

    private static NodeType getNodeTypeRef(Operator predicate) {
        if (predicate == null)
            return null;
        if (predicate instanceof And) {
            return NodeType.AND;
        }
        if (predicate instanceof Or) {
            return NodeType.OR;
        }
        if (predicate instanceof Imp) {
            return NodeType.IMP;
        }
        if (predicate instanceof Eqv) {
            return NodeType.EQV;
        }
        if (predicate instanceof Not) {
            return NodeType.NOT;
        }
        return null;
    }

    public static class MembershipFunctionTransform implements JsonDeserializer<com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction> {

        @Override
        public com.castellanos94.jfuzzylogic.core.membershipfunction.MembershipFunction deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            String str = jsonElement.getAsJsonObject().get("type").getAsString().toLowerCase();
            switch (str) {
                case "map-nominal":
                    return jsonDeserializationContext.deserialize(jsonElement, MapNominal.class);
                case "fpg":
                    return jsonDeserializationContext.deserialize(jsonElement, FPG.class);
                case "sigmoid":
                    return jsonDeserializationContext.deserialize(jsonElement, Sigmoid.class);
                case "-sigmoid":
                    return jsonDeserializationContext.deserialize(jsonElement, NSigmoid.class);
                case "singleton":
                    return jsonDeserializationContext.deserialize(jsonElement, Singleton.class);
                case "triangular":
                    return jsonDeserializationContext.deserialize(jsonElement, Triangular.class);
                case "trapezoidal":
                    return jsonDeserializationContext.deserialize(jsonElement, Trapezoidal.class);
                case "rtrapezoidal":
                    return jsonDeserializationContext.deserialize(jsonElement, RTrapezoidal.class);
                case "ltrapezoidal":
                    return jsonDeserializationContext.deserialize(jsonElement, LTrapezoidal.class);
                case "gamma":
                    return jsonDeserializationContext.deserialize(jsonElement, Gamma.class);
                case "lgamma":
                    return jsonDeserializationContext.deserialize(jsonElement, LGamma.class);
                case "pseudo-exp":
                    return jsonDeserializationContext.deserialize(jsonElement, PseudoExp.class);
                case "nominal":
                    return jsonDeserializationContext.deserialize(jsonElement, Nominal.class);
                case "zform":
                    return jsonDeserializationContext.deserialize(jsonElement, ZForm.class);
                case "sform":
                    return jsonDeserializationContext.deserialize(jsonElement, SForm.class);
                case "gaussian":
                    return jsonDeserializationContext.deserialize(jsonElement, Gaussian.class);
                default:
                    return null;
            }
        }
    }
}
